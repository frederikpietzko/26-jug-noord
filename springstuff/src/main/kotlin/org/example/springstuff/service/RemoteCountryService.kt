package org.example.springstuff.service

import com.example.soap.countries.*
import org.springframework.stereotype.Service

@Service
class RemoteCountryService {
    private val countryService: CountriesPortType by lazy {
        CountriesService().getCountriesPort()
    }

    fun send() {
        val req = GetCountryRequest()
        req.name = "Germany"
        countryService.getCountry(req)
    }

    fun sendGetAllCountries() {
        val getAllReq = getAllCountriesRequest {
            isIncludeDetails = true
            filter {
                region = "Europe"
                minPopulation = 1000000L
                maxPopulation = 100000000L
                currencies {
                    add(Currency.EUR)
                    add(Currency.GBP)
                    add(Currency.PLN)
                }
            }
        }
        countryService.getAllCountries(getAllReq)
    }
}

fun getAllCountriesRequest(block: GetAllCountriesRequest.() -> Unit) = GetAllCountriesRequest().apply(block)

fun GetAllCountriesRequest.filter(block: FilterCriteria.() -> Unit) =
    apply { filterCriteria = FilterCriteria().apply(block) }

fun FilterCriteria.currencies(block: MutableList<Currency>.() -> Unit) {
    currencies = FilterCriteria.Currencies().apply { currency.apply(block) }
}
