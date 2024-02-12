package net.helcel.beendroid

import net.helcel.beendroid.countries.Country
import net.helcel.beendroid.countries.Group
import net.helcel.beendroid.countries.Group.*

import org.junit.Assert
import org.junit.Test

class CountryTest {
    private val codes =  listOf("AFG","XAD","ALA","ALB","DZA","ASM","AND","AGO","AIA","ATG","ARG","ARM","ABW","AUS","AUT","AZE",
        "BHS","BHR","BGD","BRB","BLR","BEL","BLZ","BEN","BMU","BTN","BOL","BES","BIH","BWA","BVT","BRA", "IOT","VGB","BRN","BGR","BFA","BDI","KHM",
        "CMR","CAN","CPV","CYM","CAF","TCD","CHL","CHN","CXR","XCL","CCK","COL","COM","COK","CRI","CIV","HRV","CUB","CUW","CYP","CZE","COD",
        "DNK","DJI","DMA","DOM","ECU","EGY","SLV","GNQ","ERI","EST","ETH","FLK","FRO","FJI","FIN","FRA","GUF","PYF","ATF",
        "GAB","GMB","GEO","DEU","GHA","GIB","GRC","GRL","GRD","GLP","GUM","GTM","GGY","GIN","GNB","GUY","HTI","HMD","HND","HUN",
        "ISL","IND","IDN","IRN","IRQ","IRL","IMN","ISR","ITA","JAM","JPN","JEY","JOR","KAZ","KEN","KIR","XKO","KWT","KGZ",
        "LAO","LVA","LBN","LSO","LBR","LBY","LIE","LTU","LUX","SXM",
        "MKD","MDG","MWI","MYS","MDV","MLI","MLT","MHL","MTQ","MRT","MUS","MYT","MEX","FSM","MDA","MCO","MNG","MNE","MSR","MAR","MOZ","MMR",
        "NAM","NRU","NPL","NLD","NCL","NZL","NIC","NER","NGA","NIU","NFK","PRK","ZNC","MNP","NOR","OMN",
        "PAK","PLW","PSE","PAN","PNG","PRY","PER","PHL","PCN","POL","PRT","PRI","QAT","COG","REU","ROU","RUS","RWA","BLM","MAF",
        "SHN","KNA","LCA","SPM","VCT","WSM","SMR","STP","SAU","SEN","SRB","SYC","SLE","SGP","SVK","SVN","SLB","SOM","ZAF","SGS","KOR","SSD","ESP",
        "LKA","SDN","SUR","SJM","SWZ","SWE","CHE","SYR","TWN","TJK","TZA","THA","TLS","TGO","TKL","TON","TTO","TUN","TUR","TKM","TCA","TUV","UGA",
        "UKR","ARE","GBR","USA","UMI","URY","UZB","VUT","VAT","VEN","VNM","VIR","WLF","ESH","YEM","ZMB","ZWE")

    private val codesIgnore = listOf(
        "ATA", // Antarctica not present in dataset
        "HKG", // HongKong: Included in china ?
        "MAC", // Macao: Included in china ?
        "ANT", //Netherlands Antilles: Dissolution
    )


     @Test
     fun allCountriesInAGroup() {
         Country.entries.forEach { c ->
             val cnt = Group.entries.none {
                 it.children.contains((c))
             }
             Assert.assertEquals("$c has no group !",cnt,false)
         }
     }

    @Test
    fun allCountriesInASingleGroup() {
        Country.entries.forEach { c ->
            val cnt = listOf(EEE,FFF,ABB,NNN,SRR,UUU,XXX,ZZZ).count {
                it.children.contains((c))
            }
            Assert.assertEquals("$c is in none or multiple continents",cnt,1)
        }
    }

    @Test
    fun allCountriesFoundInEnum() {
        codes.forEach {co ->
            val r = Country.entries.map { it.code }.contains(co)
            Assert.assertEquals("$co not found in enum", r, true)
        }
    }


    @Test
    fun allCountriesFoundInImport() {
        Country.entries.forEach {
            if(codesIgnore.contains(it.code))
                return@forEach
            val r = codes.contains(it.code)
            Assert.assertEquals("$it not found in import", r, true)
        }
    }

    @Test
    fun allCountriesValidName() {
        Country.entries.forEach {
            Assert.assertEquals("$it has no full_name", it.fullName.isNotEmpty(), true)
        }
    }

    @Test
    fun allCountriesValidArea() {
        Country.entries.forEach {
            Assert.assertEquals("$it has an area of 0", it.area > 0, true)
        }
    }

    @Test
    fun allCountryGroupsValidName() {
        Group.entries.forEach {
            Assert.assertEquals("$it has no full_name", it.fullName.isNotEmpty(), true)
        }
    }

    @Test
    fun allCountryGroupsValidArea() {
        Group.entries.forEach {
            Assert.assertEquals("$it has an area of 0", it.area >= 0, true)
        }
    }

    @Test
    fun allRegionHaveCode() {
        Assert.assertEquals(EEE.code, "EEE")
    }
}