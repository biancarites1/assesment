package com.example.demo.rest.controller;

import com.example.demo.model.CryptoStatistics;
import com.example.demo.model.NormalizedValueForCrypto;
import com.example.demo.rest.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController()
@RequestMapping("/api/recommendation")
public class RecommendationController {
    @Autowired(required = false)
    private RecommendationService recommendationService;

    /**
     * Retrieves oldest/newest/min/max values for requested crypto
     *
     * @param crypto The symbol of the crypto to retrieve statistics for.
     * @return The statistics for requested crypto.
     */
    @RequestMapping( value="/statistics-for-crypto",method= RequestMethod.GET)
    public CryptoStatistics getStatisticsByCrypto(@RequestParam("crypto") String crypto, @RequestParam(value="requestedDay", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date requestedDay) {
        try {
            return recommendationService.getCryptoStatisticsForSymbol(crypto, requestedDay);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * Retrieves a descending sorted list of all the cryptos,
     *    comparing the normalized range (i.e. (max-min)/min)
     *
     * @return The list of normalized values for all cryptos.
     */
    //Exposes an endpoint that will return a descending sorted list of all the cryptos,
    //comparing the normalized range (i.e. (max-min)/min)
    @RequestMapping( value="/normalized-range",method= RequestMethod.GET)
    public List<NormalizedValueForCrypto> getAllNormalizedValues() {
        try {
            return recommendationService.getNormalizedValuesForAllCryptos(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the crypto with the highest normalized range for a
     *     specific day
     * @param day The day for which the normalized value should be computed
     * @return The crypto with the highest normalized range for a
     *      *     specific day
     */
    //Exposes an endpoint that will return the crypto with the highest normalized range for a
    //specific day
    @RequestMapping( value="/highest-normalized-range-by-day",method= RequestMethod.GET)
    public NormalizedValueForCrypto getHighestNormalizedValue(@RequestParam("day") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date day) {
        try {
            return recommendationService.getHighestNormalizedValueForCryptoByDay(day);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
