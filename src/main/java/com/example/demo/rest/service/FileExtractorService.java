package com.example.demo.rest.service;

import com.example.demo.model.CryptoData;
import com.opencsv.CSVReader;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Service
public class FileExtractorService {
    protected static final String PRICES_DIRECTORY = "/prices";
    private List<CryptoData> cryptoDataList = new ArrayList<>();

    public void setCryptoDataList(List<CryptoData> cryptoDataList) {
       this.cryptoDataList = cryptoDataList;
    }

    @Cacheable
    public List<CryptoData> getCryptoDataList() {
        return cryptoDataList;
    }

    @PostConstruct
    public void init() {
        setCryptoDataList(extractCryptoValuesFromFiles());
    }
    public Set<String> extractFileNames(String directoryName) throws IOException, URISyntaxException {
        try (Stream<Path> stream = Files.list(Paths.get(directoryName))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    protected String getDirectoryName() throws URISyntaxException {
        return Paths.get(getClass().getResource(PRICES_DIRECTORY).toURI()).toString();
    }

    private Path getFilePath(String filename) throws URISyntaxException {
        return Paths.get(getClass().getResource(PRICES_DIRECTORY + "/" + filename).toURI());
    }

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
                            Date timestamp = new Date(Long.valueOf(lines.get(i)[0].toString()));
                            CryptoData cryptoData = new CryptoData(timestamp, lines.get(i)[1].toString(), Float.valueOf(lines.get(i)[2].toString()));
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
