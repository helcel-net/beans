package net.helcel.beendroid.countries

import net.helcel.beendroid.countries.Country.*
enum class Group(override val fullName: String, override val children: List<Country>) : GeoLoc {

    EEE("Europe",listOf(
        ALB, AND, AUT, BLR, BEL, BIH, BGR, HRV, CYP, CZE, DNK, EST, FIN, FRA,
        DEU, GRC, HUN, ISL, IRL, ITA, KAZ, XKO, LVA, LIE, LTU, LUX, MLT, MDA,
        MCO, MNE, NLD, MKD, NOR, POL, PRT, ROU, RUS, SMR, SRB, SVK, SVN, ESP,
        SWE, CHE, UKR, GBR, VAT, XAD,
    )),
    ABB("Asia", listOf(
        AFG, ARM, AZE, BHR, BGD, BTN, BRN, KHM, CHN, GEO, HKG, IND, IDN, IRN,
        IRQ, ISR, JPN, JOR, KWT, KGZ, LAO, LBN, MAC, MYS, MDV, MNG, MMR,
        NPL, PRK, OMN, PAK, PSE, PHL, QAT, SAU, SGP, KOR, LKA, SYR, TWN, TJK,
        THA, TLS, TUR, TKM, ARE, UZB, VNM, YEM, ZNC,
    )),
    FFF("Africa", listOf(
        DZA, AGO, BDI, BEN, BWA, BFA, BDI, CPV, CMR, CAF, TCD, COM, COG, COD, CIV, DJI, EGY,
        GNQ, ERI, SWZ, ETH, GAB, GMB, GHA, GIN, GNB, KEN, LSO, LBR, LBY, MDG, MWI, MLI, MRT,
        MUS, MYT, MAR, MOZ, NAM, NER, NGA, COD, REU, RWA, STP, SEN, SYC, SLE, SOM, ZAF, SSD,
        SHN, SDN, TZA, TGO, TUN, UGA, COD, ZMB, ZWE,
    )),
    NNN("North America", listOf(
        ABW, AIA, ATG, BHS, BRB, BLZ, BMU, VGB, CAN, CYM, CRI, CUB, CUW, DMA,
        DOM, SLV, GRL, GRD, GLP, GTM, HTI, HND, JAM, MTQ, MEX, MSR, ANT, CUW,
        NIC, PAN, PRI, KNA, LCA, MAF, SPM, VCT, TTO, TCA, USA, XCL,
    )),
    SRR("South America", listOf(
        ARG, BOL, BRA, CHL, COL, ECU, FLK, GUF, GUY, PRY, PER, SUR, URY, VEN,
    )),
    UUU("Oceania", listOf(
        ASM, AUS, COK, FJI, PYF, GUM, KIR, MHL, FSM, NRU, NCL, NZL, NIU, NFK,
        MNP, PLW, PNG, PCN, SLB, TKL, TON, TUV, VUT, WLF,
    )),

    XXX("Others", listOf(
        ATA, // Antarctica: not in any other region
        ALA, // Åland Islands: an autonomous region of Finland, but not a member of the EU or UN
        BES, // Bonaire, Sint Eustatius and Saba: special municipalities of the Netherlands in the Caribbean
        BVT, // Bouvet Island: an uninhabited territory of Norway in the South Atlantic
        IOT, // British Indian Ocean Territory: a British overseas territory in the Indian Ocean
        CXR, // Christmas Island: an Australian external territory in the Indian Ocean
        CCK, // Cocos (Keeling) Islands: an Australian external territory in the Indian Ocean
        FRO, // Faroe Islands: an autonomous region of Denmark
        ATF, // French Southern and Antarctic Lands: a territory of France located in the southern Indian Ocean
        GIB, // Gibraltar: a British overseas territory located at the southern tip of the Iberian Peninsula
        GGY, // Guernsey: a British Crown dependency in the English Channel
        HMD, // Heard Island and McDonald Islands: an uninhabited Australian external territory in the southern Indian Ocean
        IMN, // Isle of Man: a British Crown dependency located in the Irish Sea
        JEY, // Jersey: a British Crown dependency located in the English Channel
        BLM, // Saint Barthélemy: an overseas collectivity of France in the Caribbean
        WSM, // Samoa: an independent island nation in the South Pacific
        SXM, // Sint Maarten: a constituent country of the Kingdom of the Netherlands in the Caribbean
        SGS, // South Georgia and the South Sandwich Islands: a British overseas territory in the southern Atlantic Ocean
        SJM, // Svalbard and Jan Mayen: an archipelago administered by Norway
        UMI, // United States Minor Outlying Islands: a collection of nine insular areas of the United States
        VIR, // United States Virgin Islands: an unincorporated territory of the United States in the Caribbean
        ESH  // Western Sahara: a disputed territory claimed by both Morocco and the Sahrawi Arab Democratic Republic
    )),

    ZZZ("Undefined", listOf(
    )),


    NTT("NATO", listOf(
        ALB, BEL, BGR, CAN, HRV, CZE, DNK, EST, FRA, DEU, GRC, HUN, ISL, ITA, LVA, LTU, LUX,
        MNE, NLD, NOR, POL, PRT, ROU, SVK, SVN, ESP, TUR, GBR, USA
    ))
    ;

    override val area = children.fold(0) { acc, i ->
        acc + i.area
    }

    private val isInWorld = listOf("EEE","ABB","FFF","NNN","SRR","UUU","XXX").contains(this.name)

    override val type: LocType = if (isInWorld) LocType.GROUP else LocType.CUSTOM_GROUP
    override val code = this.name


}