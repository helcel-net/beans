#!/bin/bash

LOCAL_SVG_PATH="app/src/main/assets/"

GADM_VERSION="4.1"
GADM_BASEPATH="https://geodata.ucdavis.edu/gadm"

mapshaper="./node_modules/mapshaper/bin/mapshaper"
ATA_URL="https://media.githubusercontent.com/media/wmgeolab/geoBoundaries/905b0baf5f4fb3b9ccf45293647dcacdb2b799d4/releaseData/gbOpen/ATA/ADM0/geoBoundaries-ATA-ADM0_simplified.geojson"

countries=(
  "AFG" "XAD" "ALA" "ALB" "DZA" "ASM" "AND" "AGO" "AIA" "ATG" "ARG" "ARM" "ABW" "AUS" "AUT" "AZE"
  "BHS" "BHR" "BGD" "BRB" "BLR" "BEL" "BLZ" "BEN" "BMU" "BTN" "BOL" "BES" "BIH" "BWA" "BVT" "BRA" "IOT" "VGB" "BRN" "BGR" "BFA" "BDI" "KHM"
  "CMR" "CAN" "CPV" "XCA" "CYM" "CAF" "TCD" "CHL" "CHN" "CXR" "XCL" "CCK" "COL" "COM" "COK" "CRI" "CIV" "HRV" "CUB" "CUW" "CYP" "CZE" "COD"
  "DNK" "DJI" "DMA" "DOM" "ECU" "EGY" "SLV" "GNQ" "ERI" "EST" "ETH" "FLK" "FRO" "FJI" "FIN" "FRA" "GUF" "PYF" "ATF"
  "GAB" "GMB" "GEO" "DEU" "GHA" "GIB" "GRC" "GRL" "GRD" "GLP" "GUM" "GTM" "GGY" "GIN" "GNB" "GUY" "HTI" "HMD" "HND" "HUN"
  "ISL" "IND" "IDN" "IRN" "IRQ" "IRL" "IMN" "ISR" "ITA" "JAM" "JPN" "JEY" "JOR" "KAZ" "KEN" "KIR" "XKO" "KWT" "KGZ"
  "LAO" "LVA" "LBN" "LSO" "LBR" "LBY" "LIE" "LTU" "LUX" "SXM"
  "MKD" "MDG" "MWI" "MYS" "MDV" "MLI" "MLT" "MHL" "MTQ" "MRT" "MUS" "MYT" "MEX" "FSM" "MDA" "MCO" "MNG" "MNE" "MSR" "MAR" "MOZ" "MMR"
  "NAM" "NRU" "NPL" "NLD" "NCL" "NZL" "NIC" "NER" "NGA" "NIU" "NFK" "PRK" "ZNC" "MNP" "NOR" "OMN"
  "PAK" "PLW" "PSE" "PAN" "PNG" "PRY" "PER" "PHL" "PCN" "POL" "PRT" "PRI" "QAT" "COG" "REU" "ROU" "RUS" "RWA" "BLM" "MAF"
  "SHN" "KNA" "LCA" "SPM" "VCT" "WSM" "SMR" "STP" "SAU" "SEN" "SRB" "SYC" "SLE" "SGP" "SVK" "SVN" "SLB" "SOM" "ZAF" "SGS" "KOR" "SSD" "ESP"
  "LKA" "SDN" "SUR" "SJM" "SWZ" "SWE" "CHE" "SYR" "TWN" "TJK" "TZA" "THA" "TLS" "TGO" "TKL" "TON" "TTO" "TUN" "TUR" "TKM" "TCA" "TUV" "UGA"
  "UKR" "ARE" "GBR" "USA" "UMI" "URY" "UZB" "VUT" "VAT" "VEN" "VNM" "VIR" "WLF" "ESH" "YEM" "ZMB" "ZWE"
)


url_0() {
    local country="$1"
    echo "${GADM_BASEPATH}/gadm${GADM_VERSION}/json/gadm${GADM_VERSION//./}_${country}_0.json"
}

download_0() {
    local url=$(url_0 "$1")
    local output_dir="./temp/0"
    mkdir -p "$output_dir"
    if [ -f "$output_dir/$1.json" ]; then
        echo "File Exists: $1"
    else
        echo "File Download: $1"
        wget -q -O "$output_dir/$1.json" "$url"
    fi
}


url_1() {
    local country="$1"
    echo "${GADM_BASEPATH}/gadm${GADM_VERSION}/json/gadm${GADM_VERSION//./}_${country}_1.json.zip"
}

download_1() {
    local url=$(url_1 "$1")
    local output_dir="./temp/1"
    mkdir -p "$output_dir"
    if [ -f "$output_dir/$1.json.zip" ]; then
        echo "File Exists: $1"
    else
        echo "File Download: $1"
        wget -q -O "$output_dir/$1.json.zip" "$url"
    fi
    if [ -f "$output_dir/gadm41_${1}_1.json" ]; then
        echo "File Exists: $1"
    else
        echo "File Extract: $1"
        unzip -q -o "$output_dir/$1.json.zip" -d "$output_dir"
    fi
    if [ -f "$output_dir/gadm41_${1}_1.json" ]; then
        jq '.features[] |= . + {properties: (.properties | .GID_1 = (.GID_0 + "_" + (
            if .HASC_1 != "NA" then (.HASC_1 | split(".") | .[-1])
            elif .ISO_1 != "NA" then (.ISO_1 | split("-") | .[-1])
            elif .CC_1 != "NA" then (.CC_1)
            elif .NAME_1 != "NA" then (.NAME_1)
            elif .GID_1 != "NA" then (.GID_1 | split(".") | .[-1])
            else .GID_1
            end
            )))}' "$output_dir/gadm41_${1}_1.json"  > "$output_dir/$1.json.1"
        sed -E 's/"[gadm41_]*([A-Z]*)_1"/"\1"/g' "$output_dir/$1.json.1" > "$output_dir/$1.json"
    fi
}


toSVG_0() {
    local input_files=("ATA")

    for country in "${countries[@]}"
    do
        input_file="./temp/0/${country}.json"
        if [ -f "$input_file" ]; then
            input_files+=("$input_file")
        else
            echo "Input file $input_file not found."
        fi
    done


    "$mapshaper" -i combine-files ${input_files[@]} -proj webmercator -simplify 0.005 weighted keep-shapes resolution=1200x1200 -o ./app/src/main/assets/mercator0.svg svg-data=GID_0,COUNTRY id-field=GID_0
    "$mapshaper" -i combine-files ${input_files[@]} -proj aeqd +lat_0=90 -simplify 0.005 weighted keep-shapes resolution=1200x1200 -o ./app/src/main/assets/aeqd0.svg svg-data=GID_0,COUNTRY id-field=GID_0
}

toSVG_1() {
    input_files=("ATA")

    for country in "${countries[@]}"
    do
        input_file="./temp/1/${country}.json"
        # input_file="./temp/1/gadm41_${country}_1.json"
        if [ -f "$input_file" ]; then
            input_files+=("$input_file")
        else
            echo "Input file $input_file not found."
        fi
    done

    "$mapshaper" -i combine-files ${input_files[@]} -proj webmercator -simplify 0.005 weighted keep-shapes resolution=1200x1200  -o ./app/src/main/assets/mercator1.svg svg-data=GID_0,COUNTRY,GID,NAME id-field=GID
    "$mapshaper" -i combine-files ${input_files[@]} -proj aeqd +lat_0=90 -simplify 0.005 weighted keep-shapes resolution=1200x1200 -o ./app/src/main/assets/aeqd1.svg svg-data=GID_0,COUNTRY,GID,NAME id-field=GID
}


toSVG_01() {
    input_files=("./temp/1/ATA.json" "./temp/0/ATA.json")

    for country in "${countries[@]}"
    do
        input_file1="./temp/1/${country}.json"
        input_file0="./temp/0/${country}.json"
        if [ -f "$input_file1" ]; then
            input_files+=("$input_file1")
        fi
        if [ -f "$input_file0" ]; then
            input_files+=("$input_file0")
        fi
    done


    "$mapshaper" -i combine-files ${input_files[@]} snap -proj eqdc +lat_1=55 +lat_2=60 -simplify 0.005 weighted keep-shapes resolution=1200x1200  -o ./app/src/main/assets/eqdc01.svg svg-data=GID_0,COUNTRY,GID,NAME id-field=GID
    "$mapshaper" -i combine-files ${input_files[@]} snap -proj loxim densify -simplify 0.005 weighted keep-shapes resolution=1200x1200  -o ./app/src/main/assets/loxim01.svg svg-data=GID_0,COUNTRY,GID,NAME id-field=GID
    "$mapshaper" -i combine-files ${input_files[@]} snap -proj webmercator densify -simplify 0.005 weighted keep-shapes resolution=1200x1200  -o ./app/src/main/assets/webmercator01.svg svg-data=GID_0,COUNTRY,GID,NAME id-field=GID
    "$mapshaper" -i combine-files ${input_files[@]} snap -proj aeqd +lat_0=90 densify -simplify 0.005 weighted keep-shapes resolution=1200x1200 -o ./app/src/main/assets/aeqd01.svg svg-data=GID_0,COUNTRY,GID,NAME id-field=GID
}

do_1() {
    for country in "${countries[@]}"
    do
        download_1 "$country"
    done
    wget -q -O "./temp/1/ATA.json" "$ATA_URL"
}
do_0() {
    for country in "${countries[@]}"
    do
        download_0 "$country"
    done
    wget -q -O "./temp/1/ATA.json" "$ATA_URL"
}
do_0
do_1
# toSVG_0
# toSVG_1
toSVG_01