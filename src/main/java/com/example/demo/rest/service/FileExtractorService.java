package com.example.demo.rest.service;

import com.example.demo.AppConfig;
import com.example.demo.model.CryptoData;
import com.opencsv.CSVReader;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Service
public class FileExtractorService {
    private List<CryptoData> cryptoDataList = new ArrayList<>();

    @Autowired
    private AppConfig appConfig;

    public void setCryptoDataList(List<CryptoData> cryptoDataList) {
       this.cryptoDataList = cryptoDataList;
    }

    @Cacheable
    public List<CryptoData> getCryptoDataList() {
        return cryptoDataList;
    }

    /*all csv files will be read when the service starts and their content will be stored in a list of objects,
    * each object containing a timestamp, a symbol(the currency) and the price.Retrieval of the list will be done from cache to improve performance.
    * */
    @PostConstruct
    public void init() {
        setCryptoDataList(extractCryptoValuesFromFiles());
    }
    //extract all file names from a given directory(this is the directory from resources folder where all the crypto csv files are stored
    public Set<String> extractFileNames(String directoryName) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(directoryName))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    protected String getDirectoryName() throws URISyntaxException {
        return Paths.get(Objects.requireNonNull(getClass().getResource(appConfig.getCryptoDirectory())).toURI()).toString();
    }

    private Path getFilePath(String filename) throws URISyntaxException {
        return Paths.get(Objects.requireNonNull(getClass().getResource(appConfig.getCryptoDirectory() + "/" + filename)).toURI());
    }

    //read content of each csv file and store it in a list of string arrays
    public List<String[]> readCsvFile(String filename) throws Exception {
        Path filePath = getFilePath(filename);
        List<String[]> list = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    list.add(line);
                }
            }
        }
        return list;
    }

    //for each csv file, read it's content and store the values in object with fields: timestamp, crypto symbol(BTC, DOGE, etc.) and price value
    public List<CryptoData> extractCryptoValuesFromFiles(){
        List<CryptoData> cryptoDataList = new ArrayList<>();
        try {
            Set<String> filenames = extractFileNames(getDirectoryName());
            filenames.forEach(filename -> {
                try {
                    List<String[]> lines = readCsvFile(filename);
                    //extract values from each file and ignore first line of each file which is the header
                    for(int i=1;i<lines.size(); i++){
                        if(lines.get(i) !=null){
                            Date timestamp = new Date(Long.parseLong(lines.get(i)[0]));
                            CryptoData cryptoData = new CryptoData(timestamp, lines.get(i)[1], Float.parseFloat(lines.get(i)[2]));
                            cryptoDataList.add(cryptoData);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
           return cryptoDataList;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
