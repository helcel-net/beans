package net.helcel.beans.countries

import net.helcel.beans.countries.Country.*

enum class Group(override val fullName: String, override val children: Set<GeoLoc>) : GeoLoc {

    EEE(
        "Europe", setOf(
            ALA,// Åland Islands: an autonomous region of Finland, but not a member of the EU or UN
            ALB,
            AND,
            AUT,
            BLR,
            BEL,
            BIH,
            BGR,
            HRV,
            CZE,
            DNK,
            EST,
            FRO, // Faroe Islands: an autonomous region of Denmark
            FIN,
            FRA,
            DEU,
            GIB, // Gibraltar: a British overseas territory located at the southern tip of the Iberian Peninsula
            GRC,
            GRL,
            GGY, // Guernsey: a British Crown dependency in the English Channel
            HUN,
            ISL,
            IRL,
            IMN, // Isle of Man: a British Crown dependency located in the Irish Sea
            ITA,
            JEY, // Jersey: a British Crown dependency located in the English Channel
            XKO,
            LVA,
            LIE,
            LTU,
            LUX,
            MLT,
            MDA,
            MCO,
            MNE,
            NLD,
            MKD,
            NOR,
            POL,
            PRT,
            ROU,
            RUS,
            SMR,
            SRB,
            SVK,
            SVN,
            ESP,
            SJM, // Svalbard and Jan Mayen: an archipelago administered by Norway
            SWE,
            CHE,
            UKR,
            GBR,
            VAT,
        )
    ),
    ABB(
        "Asia", setOf(
            XAD,
            AFG,
            ARM,
            AZE,
            BHR,
            BGD,
            BTN,
            IOT, // British Indian Ocean Territory: a British overseas territory in the Indian Ocean
            BRN,
            KHM,
            CCK, // Cocos (Keeling) Islands: an Australian external territory in the Indian Ocean
            CHN,
            CXR, // Christmas Island: an Australian external territory in the Indian Ocean
            CYP,
            GEO,
            //HKG,
            IND,
            IDN,
            IRN,
            IRQ,
            ISR,
            JPN,
            JOR,
            KAZ,
            KWT,
            KGZ,
            LAO,
            LBN,
            //MAC,
            MYS,
            MDV,
            MNG,
            MMR,
            NPL,
            PRK,
            OMN,
            PAK,
            PSE,
            PHL,
            QAT,
            SAU,
            SGP,
            KOR,
            LKA,
            SYR,
            TWN,
            TJK,
            THA,
            TLS,
            TUR,
            TKM,
            ARE,
            UZB,
            VNM,
            YEM,
            ZNC,
        )
    ),
    FFF(
        "Africa", setOf(
            DZA,
            AGO,
            BDI,
            BEN,
            BWA,
            BVT, // Bouvet Island: an uninhabited territory of Norway in the South Atlantic
            BFA,
            BDI,
            CPV,
            CMR,
            CAF,
            TCD,
            COM,
            COG,
            COD,
            CIV,
            DJI,
            EGY,
            GNQ,
            ERI,
            ETH,
            ATF, // French Southern and Antarctic Lands: a territory of France located in the southern Indian Ocean
            GAB,
            GMB,
            GHA,
            GIN,
            GNB,
            HMD, // Heard Island and McDonald Islands: an uninhabited Australian external territory in the southern Indian Ocean
            KEN,
            LSO,
            LBR,
            LBY,
            MDG,
            MWI,
            MLI,
            MRT,
            MUS,
            MYT,
            MAR,
            MOZ,
            NAM,
            NER,
            NGA,
            COD,
            REU,
            RWA,
            STP,
            SEN,
            SYC,
            SLE,
            SOM,
            ZAF,
            SSD,
            SHN,
            SDN,
            SWZ,
            TZA,
            TGO,
            TUN,
            UGA,
            COD,
            ZMB,
            ZWE,
            ESH,
        )
    ),
    NNN(
        "North America", setOf(
            ABW,
            AIA,
            ATG,
            BHS,
            BRB,
            BLZ,
            BMU,
            BES, // Bonaire, Sint Eustatius and Saba: special municipalities of the Netherlands in the Caribbean
            VGB,
            CAN,
            CYM,
            XCL,
            CRI,
            CUB,
            CUW,
            DMA,
            DOM,
            SLV,
            GRD,
            GLP,
            GTM,
            HTI,
            HND,
            JAM,
            MTQ,
            MEX,
            MSR,
            //ANT,
            CUW,
            NIC,
            PAN,
            PRI,
            BLM, // Saint Barthélemy: an overseas collectivity of France in the Caribbean
            KNA,
            LCA,
            MAF,
            SPM,
            VCT,
            SXM, // Sint Maarten: a constituent country of the Kingdom of the Netherlands in the Caribbean
            TTO,
            TCA,
            USA,
            UMI, // United States Minor Outlying Islands: a collection of nine insular areas of the United States
            VIR, // United States Virgin Islands: an unincorporated territory of the United States in the Caribbean
        )
    ),
    SRR(
        "South America", setOf(
            ARG,
            BOL,
            BRA,
            CHL,
            COL,
            ECU,
            FLK,
            GUF,
            GUY,
            PRY,
            PER,
            SGS, // South Georgia and the South Sandwich Islands: a British overseas territory in the southern Atlantic Ocean
            SUR,
            URY,
            VEN,
        )
    ),
    UUU(
        "Oceania", setOf(
            ASM,
            AUS,
            COK,
            FJI,
            PYF,
            GUM,
            KIR,
            MHL,
            FSM,
            NRU,
            NCL,
            NZL,
            NIU,
            NFK,
            MNP,
            PLW,
            PNG,
            PCN,
            WSM, // Samoa: an independent island nation in the South Pacific
            SLB,
            TKL,
            TON,
            TUV,
            VUT,
            WLF,
        )
    ),

    XXX(
        "Other", setOf(
           
        )
    ),

    ZZZ(
        "Undefined", setOf(
        )
    ),


    NTT(
        "NATO", setOf(
            ALB, BEL, BGR, CAN, HRV, CZE, DNK, EST, FRA, DEU, GRC, HUN, ISL, ITA, LVA, LTU, LUX,
            MNE, NLD, NOR, POL, PRT, ROU, SVK, SVN, ESP, TUR, GBR, USA
        )
    );

    override val area = children.fold(0) { acc, i ->
        acc + i.area
    }

    private val isInWorld =
        listOf("EEE", "ABB", "FFF", "NNN", "SRR", "UUU", "XXX").contains(this.name)

    override val type = if (isInWorld) GeoLoc.LocType.GROUP else GeoLoc.LocType.CUSTOM_GROUP
    override val code = this.name


}