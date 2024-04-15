package com.example.demo.rest.service;

import com.example.demo.AppConfig;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.CryptoData;
import com.example.demo.model.CryptoStatistics;
import com.example.demo.model.NormalizedValueForCrypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;



@Service
public class RecommendationService {
    @Autowired
    private FileExtractorService fileExtractorService;
    @Autowired
    private AppConfig appConfig;

    @Cacheable
    /*for all supported currencies defined in csv files generate statistics with min,max price and oldest/newest date*/
    public List<CryptoStatistics> generateAllCryptoStatistics(List<CryptoData> cryptoDataList, Date startingDateToComputeStatistics, Date endingDateToComputeStatistics) {
        List<String> symbols = getCryptoSymbols(cryptoDataList);
        List<CryptoStatistics> cryptoStatisticsList = new ArrayList<>();
        for (String symbol : symbols) {
            cryptoStatisticsList.add(generateStatisticsForCrypto(cryptoDataList, symbol, startingDateToComputeStatistics, endingDateToComputeStatistics));
        }
        return cryptoStatisticsList;
    }

    /*extract from the list of statistics the statistic for the crypto requested*/
    public CryptoStatistics getCryptoStatisticsForSymbol(String symbol, Date startingDateToComputeStatistics, Date endingDateToComputeStatistics) {
        return generateAllCryptoStatistics(validateCryptoDataList(), startingDateToComputeStatistics, endingDateToComputeStatistics).stream().filter((cryptoStatistics -> cryptoStatistics.getCrypto().equals(symbol))).findFirst().get();
    }

    /*extract from the statistics list the normalized value computed for each crypto and sort the list of cryptos in descending order of the normalized value*/
    public List<NormalizedValueForCrypto> getNormalizedValuesForAllCryptos(Date startingDateToComputeStatistics, Date endingDateToComputeStatistics) {
        List<CryptoStatistics> cryptoStatisticsList = generateAllCryptoStatistics(validateCryptoDataList(), startingDateToComputeStatistics, endingDateToComputeStatistics);
        cryptoStatisticsList.sort(((Comparator<CryptoStatistics>) (o1, o2) -> Float.compare(o1.getNormalizedRange(), o2.getNormalizedRange())).reversed());
        return cryptoStatisticsList.stream().map(statistics -> new NormalizedValueForCrypto(statistics.getCrypto(), statistics.getNormalizedRange())).collect(Collectors.toList());

    }

    /*extract from the statistics list the highest normalized value computed, by extracting the first element of the descending list of all normalized values for cryptos computed*/
    public NormalizedValueForCrypto getHighestNormalizedValueForCryptoByDay(Date day) {
        List<NormalizedValueForCrypto> normalizedValueForCryptos = getNormalizedValuesForAllCryptos(day, day);
        return normalizedValueForCryptos.get(0);
    }

    /*method to validate that csv files contain crypto data and are not empty.*/
    private List<CryptoData> validateCryptoDataList() {
        List<CryptoData> cryptoDataList = fileExtractorService.getCryptoDataList();
        if (cryptoDataList.isEmpty()) {
            throw new BadRequestException("Crypto data files are empty");
        }
        return cryptoDataList;
    }

    /*method to extract all prices values for all cryptos stored in csv files*/
    private List<Float> getCryptoPrices(List<CryptoData> cryptoDataList, String crypto) {
        return cryptoDataList.stream().filter(cryptoData -> cryptoData.getSymbol().equals(crypto)).map(CryptoData::getPrice).collect(Collectors.toList());
    }

    /*method to extract all cryptocurrencies stored in csv files with validation for allowed cryptos configured in application.properties file
    * If new csv files are loaded with new currency, that currency should be added to application.properties file.In this way we can control which are the supported currencies for the app and prevent user
    * to have access to unsupported currencies.
    * Currently, we allow user to read only supported currencies, but if needed we can throw an error if csv files contain unsupported currencies, like in the commented lines below.*/

    private List<String> getCryptoSymbols(List<CryptoData> cryptoDataList) {
        List<String> supportedCryptos  = Arrays.asList(appConfig.getAllowedCryptos().split(","));
        List<String> cryptoSymbols = cryptoDataList.stream().map(CryptoData::getSymbol).distinct().toList();
        return cryptoSymbols.stream().filter(supportedCryptos::contains).collect(Collectors.toList());
//        List<String> notFoundEntries = cryptoSymbols.stream()
//                .filter(entry -> !supportedCryptos.contains(entry))
//                .collect(Collectors.toList());
//
//        if (!notFoundEntries.isEmpty()) {
//            throw new IllegalArgumentException("Cryptos not supported yet: " + notFoundEntries);
//        }
//        return cryptoSymbols;
    }

    private List<Date> getCryptoTimestamps(List<CryptoData> cryptoDataList, String crypto) {
        return cryptoDataList.stream().filter(cryptoData -> cryptoData.getSymbol().equals(crypto)).map(CryptoData::getTimestamp).collect(Collectors.toList());
    }

    /*calculate for each currency the maximum price, minimum price, oldest date and newest date and the normalized value (which is the (maxPrice - minPrice) / minPrice)
    for all entries stored in files(all files contain values for current month, if more data is loaded this can be configurable to extract date for a particular time range)*/
    private static CryptoStatistics buildCryptoStatisticsForCrypto(String crypto, List<Date> timestamps, List<Float> prices) {
        Date oldestDate = null, newestDate = null;
        float minPrice = 0, maxPrice = 0, normalizedRange = 0;
        if (!timestamps.isEmpty()) {
            oldestDate = Collections.min(timestamps);
            newestDate = Collections.max(timestamps);
        }
        if (!prices.isEmpty()) {
            minPrice = Collections.min(prices);
            maxPrice = Collections.max(prices);
            normalizedRange = (maxPrice - minPrice) / minPrice;
        }
        return new CryptoStatistics(crypto, oldestDate, newestDate, minPrice, maxPrice, normalizedRange);
    }


    /*for each cryptocurrency extracted from csv files generate a statistic object containing maximum price, minimum price, oldest and newest date for a period of time.
    * If no starting or ending date is provided the statistics will be computed for all time ranges specified in the files.
    * If starting date is provided statistics will be computed for dates starting after startingDateToCompute
    * If ending date is provided statistics will be computed for dates before endingDateToCompute
     * */
    private CryptoStatistics generateStatisticsForCrypto(List<CryptoData> cryptoDataList, String crypto, Date startingDateToComputeStatistics, Date endingDateToComputeStatistics) {
        List<Date> timestamps = getCryptoTimestamps(cryptoDataList, crypto);
        List<Float> prices = getCryptoPrices(cryptoDataList, crypto);
        if (startingDateToComputeStatistics != null) {
            timestamps = timestamps.stream().filter(timestamp -> timestamp.after(startingDateToComputeStatistics) || timestamp.equals(startingDateToComputeStatistics)).collect(Collectors.toList());
        }
        if (endingDateToComputeStatistics != null) {
            timestamps = timestamps.stream().filter(timestamp -> timestamp.before(endingDateToComputeStatistics) || timestamp.equals(endingDateToComputeStatistics)).collect(Collectors.toList());
        }
        return buildCryptoStatisticsForCrypto(crypto, timestamps, prices);
    }

}
