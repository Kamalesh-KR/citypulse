package com.citypulse.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/global")
public class GlobalMapController {

    private static final Map<String, CountryData> COUNTRIES = new HashMap<>();
    private static final Map<String, List<CityData>> COUNTRY_CITIES = new HashMap<>();
    private static final Map<String, TrafficProfile> COUNTRY_TRAFFIC = new HashMap<>();

    static {
        // ============================================
        // ASIA
        // ============================================
        add("India", "IN", 20.5937, 78.9629, "Asia", "New Delhi", 1407563842L, "INR", "Asia/Kolkata");
        add("China", "CN", 35.8617, 104.1954, "Asia", "Beijing", 1412600000L, "CNY", "Asia/Shanghai");
        add("Japan", "JP", 36.2048, 138.2529, "Asia", "Tokyo", 125681593L, "JPY", "Asia/Tokyo");
        add("South Korea", "KR", 35.9078, 127.7669, "Asia", "Seoul", 51744876L, "KRW", "Asia/Seoul");
        add("Indonesia", "ID", -0.7893, 113.9213, "Asia", "Jakarta", 273753191L, "IDR", "Asia/Jakarta");
        add("Pakistan", "PK", 30.3753, 69.3451, "Asia", "Islamabad", 231402117L, "PKR", "Asia/Karachi");
        add("Bangladesh", "BD", 23.6850, 90.3563, "Asia", "Dhaka", 169356251L, "BDT", "Asia/Dhaka");
        add("Vietnam", "VN", 14.0583, 108.2772, "Asia", "Hanoi", 97468029L, "VND", "Asia/Ho_Chi_Minh");
        add("Thailand", "TH", 15.8700, 100.9925, "Asia", "Bangkok", 71601103L, "THB", "Asia/Bangkok");
        add("Malaysia", "MY", 4.2105, 101.9758, "Asia", "Kuala Lumpur", 33938221L, "MYR", "Asia/Kuala_Lumpur");
        add("Singapore", "SG", 1.3521, 103.8198, "Asia", "Singapore", 5453600L, "SGD", "Asia/Singapore");
        add("Philippines", "PH", 12.8797, 121.7740, "Asia", "Manila", 113880328L, "PHP", "Asia/Manila");
        add("Myanmar", "MM", 21.9162, 95.9560, "Asia", "Naypyidaw", 54817919L, "MMK", "Asia/Yangon");
        add("Cambodia", "KH", 12.5657, 104.9910, "Asia", "Phnom Penh", 16946438L, "KHR", "Asia/Phnom_Penh");
        add("Laos", "LA", 19.8563, 102.4955, "Asia", "Vientiane", 7425057L, "LAK", "Asia/Vientiane");
        add("Nepal", "NP", 28.3949, 84.1240, "Asia", "Kathmandu", 30034989L, "NPR", "Asia/Kathmandu");
        add("Sri Lanka", "LK", 7.8731, 80.7718, "Asia", "Colombo", 21919000L, "LKR", "Asia/Colombo");
        add("Bhutan", "BT", 27.5142, 90.4336, "Asia", "Thimphu", 777486L, "BTN", "Asia/Thimphu");
        add("Maldives", "MV", 3.2028, 73.2207, "Asia", "Male", 521457L, "MVR", "Indian/Maldives");
        add("Afghanistan", "AF", 33.9391, 67.7100, "Asia", "Kabul", 41128771L, "AFN", "Asia/Kabul");
        add("Kazakhstan", "KZ", 48.0196, 66.9237, "Asia", "Astana", 19196465L, "KZT", "Asia/Almaty");
        add("Uzbekistan", "UZ", 41.3775, 64.5853, "Asia", "Tashkent", 35648100L, "UZS", "Asia/Tashkent");
        add("Turkmenistan", "TM", 38.9697, 59.5563, "Asia", "Ashgabat", 6341855L, "TMT", "Asia/Ashgabat");
        add("Kyrgyzstan", "KG", 41.2044, 74.7661, "Asia", "Bishkek", 6735347L, "KGS", "Asia/Bishkek");
        add("Tajikistan", "TJ", 38.8610, 71.2761, "Asia", "Dushanbe", 9750064L, "TJS", "Asia/Dushanbe");
        add("Mongolia", "MN", 46.8625, 103.8467, "Asia", "Ulaanbaatar", 3398366L, "MNT", "Asia/Ulaanbaatar");

        // ============================================
        // MIDDLE EAST
        // ============================================
        add("United Arab Emirates", "AE", 23.4241, 53.8478, "Middle East", "Abu Dhabi", 9365145L, "AED", "Asia/Dubai");
        add("Saudi Arabia", "SA", 23.8859, 45.0792, "Middle East", "Riyadh", 35950396L, "SAR", "Asia/Riyadh");
        add("Qatar", "QA", 25.3548, 51.1839, "Middle East", "Doha", 2688235L, "QAR", "Asia/Qatar");
        add("Kuwait", "KW", 29.3117, 47.4818, "Middle East", "Kuwait City", 4250114L, "KWD", "Asia/Kuwait");
        add("Bahrain", "BH", 26.0667, 50.5577, "Middle East", "Manama", 1701583L, "BHD", "Asia/Bahrain");
        add("Oman", "OM", 21.4735, 55.9754, "Middle East", "Muscat", 4520471L, "OMR", "Asia/Muscat");
        add("Yemen", "YE", 15.5527, 48.5164, "Middle East", "Sana'a", 32981641L, "YER", "Asia/Aden");
        add("Jordan", "JO", 30.5852, 36.2384, "Middle East", "Amman", 11148278L, "JOD", "Asia/Amman");
        add("Lebanon", "LB", 33.8547, 35.8623, "Middle East", "Beirut", 6769151L, "LBP", "Asia/Beirut");
        add("Syria", "SY", 34.8021, 38.9968, "Middle East", "Damascus", 17500657L, "SYP", "Asia/Damascus");
        add("Iraq", "IQ", 33.2232, 43.6793, "Middle East", "Baghdad", 43533592L, "IQD", "Asia/Baghdad");
        add("Iran", "IR", 32.4279, 53.6880, "Middle East", "Tehran", 88550570L, "IRR", "Asia/Tehran");
        add("Israel", "IL", 31.0461, 34.8516, "Middle East", "Jerusalem", 9557500L, "ILS", "Asia/Jerusalem");
        add("Turkey", "TR", 38.9637, 35.2433, "Middle East", "Ankara", 84775404L, "TRY", "Europe/Istanbul");

        // ============================================
        // EUROPE
        // ============================================
        add("United Kingdom", "GB", 55.3781, -3.4360, "Europe", "London", 67736802L, "GBP", "Europe/London");
        add("Germany", "DE", 51.1657, 10.4515, "Europe", "Berlin", 83240525L, "EUR", "Europe/Berlin");
        add("France", "FR", 46.2276, 2.2137, "Europe", "Paris", 67749632L, "EUR", "Europe/Paris");
        add("Italy", "IT", 41.8719, 12.5674, "Europe", "Rome", 59109668L, "EUR", "Europe/Rome");
        add("Spain", "ES", 40.4637, -3.7492, "Europe", "Madrid", 47415750L, "EUR", "Europe/Madrid");
        add("Portugal", "PT", 39.3999, -8.2245, "Europe", "Lisbon", 10305564L, "EUR", "Europe/Lisbon");
        add("Netherlands", "NL", 52.1326, 5.2913, "Europe", "Amsterdam", 17590672L, "EUR", "Europe/Amsterdam");
        add("Belgium", "BE", 50.5039, 4.4699, "Europe", "Brussels", 11611419L, "EUR", "Europe/Brussels");
        add("Switzerland", "CH", 46.8182, 8.2275, "Europe", "Bern", 8703405L, "CHF", "Europe/Zurich");
        add("Austria", "AT", 47.5162, 14.5501, "Europe", "Vienna", 9042495L, "EUR", "Europe/Vienna");
        add("Sweden", "SE", 60.1282, 18.6435, "Europe", "Stockholm", 10415811L, "SEK", "Europe/Stockholm");
        add("Norway", "NO", 60.4720, 8.4689, "Europe", "Oslo", 5408320L, "NOK", "Europe/Oslo");
        add("Denmark", "DK", 56.2639, 9.5018, "Europe", "Copenhagen", 5856733L, "DKK", "Europe/Copenhagen");
        add("Finland", "FI", 61.9241, 25.7482, "Europe", "Helsinki", 5531017L, "EUR", "Europe/Helsinki");
        add("Ireland", "IE", 53.1424, -7.6921, "Europe", "Dublin", 4986528L, "EUR", "Europe/Dublin");
        add("Poland", "PL", 51.9194, 19.1451, "Europe", "Warsaw", 37747124L, "PLN", "Europe/Warsaw");
        add("Czech Republic", "CZ", 49.8175, 15.4730, "Europe", "Prague", 10516707L, "CZK", "Europe/Prague");
        add("Slovakia", "SK", 48.6690, 19.6990, "Europe", "Bratislava", 5447622L, "EUR", "Europe/Bratislava");
        add("Hungary", "HU", 47.1625, 19.5033, "Europe", "Budapest", 9749763L, "HUF", "Europe/Budapest");
        add("Romania", "RO", 45.9432, 24.9668, "Europe", "Bucharest", 19238566L, "RON", "Europe/Bucharest");
        add("Bulgaria", "BG", 42.7339, 25.4858, "Europe", "Sofia", 6885599L, "BGN", "Europe/Sofia");
        add("Greece", "GR", 39.0742, 21.8243, "Europe", "Athens", 10718565L, "EUR", "Europe/Athens");
        add("Croatia", "HR", 45.1000, 15.2000, "Europe", "Zagreb", 4076246L, "HRK", "Europe/Zagreb");
        add("Serbia", "RS", 44.0165, 21.0059, "Europe", "Belgrade", 6914178L, "RSD", "Europe/Belgrade");
        add("Ukraine", "UA", 48.3794, 31.1656, "Europe", "Kyiv", 43431432L, "UAH", "Europe/Kiev");
        add("Russia", "RU", 61.5240, 105.3188, "Europe", "Moscow", 145805947L, "RUB", "Europe/Moscow");
        add("Iceland", "IS", 64.9631, -19.0208, "Europe", "Reykjavik", 366425L, "ISK", "Atlantic/Reykjavik");
        add("Luxembourg", "LU", 49.8153, 6.1296, "Europe", "Luxembourg", 632275L, "EUR", "Europe/Luxembourg");
        add("Malta", "MT", 35.9375, 14.3754, "Europe", "Valletta", 514564L, "EUR", "Europe/Malta");
        add("Cyprus", "CY", 35.1264, 33.4299, "Europe", "Nicosia", 1244188L, "EUR", "Asia/Nicosia");
        add("Estonia", "EE", 58.5953, 25.0136, "Europe", "Tallinn", 1328976L, "EUR", "Europe/Tallinn");
        add("Latvia", "LV", 56.8796, 24.6032, "Europe", "Riga", 1879383L, "EUR", "Europe/Riga");
        add("Lithuania", "LT", 55.1694, 23.8813, "Europe", "Vilnius", 2794702L, "EUR", "Europe/Vilnius");
        add("Slovenia", "SI", 46.1512, 14.9955, "Europe", "Ljubljana", 2108977L, "EUR", "Europe/Ljubljana");
        add("Albania", "AL", 41.1533, 20.1683, "Europe", "Tirana", 2870913L, "ALL", "Europe/Tirane");
        add("North Macedonia", "MK", 41.6086, 21.7453, "Europe", "Skopje", 2083374L, "MKD", "Europe/Skopje");
        add("Bosnia and Herzegovina", "BA", 43.9159, 17.6791, "Europe", "Sarajevo", 3270943L, "BAM", "Europe/Sarajevo");
        add("Montenegro", "ME", 42.7087, 19.3744, "Europe", "Podgorica", 621306L, "EUR", "Europe/Podgorica");
        add("Moldova", "MD", 47.4116, 28.3699, "Europe", "Chisinau", 2617820L, "MDL", "Europe/Chisinau");
        add("Belarus", "BY", 53.7098, 27.9534, "Europe", "Minsk", 9432800L, "BYN", "Europe/Minsk");

        // ============================================
        // AMERICAS
        // ============================================
        add("United States", "US", 37.0902, -95.7129, "Americas", "Washington, D.C.", 331893745L, "USD", "America/New_York");
        add("Canada", "CA", 56.1304, -106.3468, "Americas", "Ottawa", 38246108L, "CAD", "America/Toronto");
        add("Mexico", "MX", 23.6345, -102.5528, "Americas", "Mexico City", 126705138L, "MXN", "America/Mexico_City");
        add("Brazil", "BR", -14.2350, -51.9253, "Americas", "Brasilia", 214326223L, "BRL", "America/Sao_Paulo");
        add("Argentina", "AR", -38.4161, -63.6167, "Americas", "Buenos Aires", 45808747L, "ARS", "America/Argentina/Buenos_Aires");
        add("Chile", "CL", -35.6751, -71.5430, "Americas", "Santiago", 19603733L, "CLP", "America/Santiago");
        add("Colombia", "CO", 4.5709, -74.2973, "Americas", "Bogota", 51516562L, "COP", "America/Bogota");
        add("Peru", "PE", -9.1900, -75.0152, "Americas", "Lima", 33715471L, "PEN", "America/Lima");
        add("Venezuela", "VE", 6.4238, -66.5897, "Americas", "Caracas", 28199867L, "VES", "America/Caracas");
        add("Ecuador", "EC", -1.8312, -78.1834, "Americas", "Quito", 17797737L, "USD", "America/Guayaquil");
        add("Bolivia", "BO", -16.2902, -63.5887, "Americas", "Sucre", 12079472L, "BOB", "America/La_Paz");
        add("Paraguay", "PY", -23.4425, -58.4438, "Americas", "Asuncion", 6780744L, "PYG", "America/Asuncion");
        add("Uruguay", "UY", -32.5228, -55.7658, "Americas", "Montevideo", 3426260L, "UYU", "America/Montevideo");
        add("Guyana", "GY", 4.8604, -58.9302, "Americas", "Georgetown", 804567L, "GYD", "America/Guyana");
        add("Suriname", "SR", 3.9193, -56.0278, "Americas", "Paramaribo", 612985L, "SRD", "America/Paramaribo");
        add("Cuba", "CU", 21.5218, -77.7812, "Americas", "Havana", 11256372L, "CUP", "America/Havana");
        add("Jamaica", "JM", 18.1096, -77.2975, "Americas", "Kingston", 2827695L, "JMD", "America/Jamaica");
        add("Haiti", "HT", 18.9712, -72.2852, "Americas", "Port-au-Prince", 11447569L, "HTG", "America/Port-au-Prince");
        add("Dominican Republic", "DO", 18.7357, -70.1627, "Americas", "Santo Domingo", 11117873L, "DOP", "America/Santo_Domingo");
        add("Panama", "PA", 8.5380, -80.7821, "Americas", "Panama City", 4351267L, "PAB", "America/Panama");
        add("Costa Rica", "CR", 9.7489, -83.7534, "Americas", "San Jose", 5153957L, "CRC", "America/Costa_Rica");
        add("Nicaragua", "NI", 12.8654, -85.2072, "Americas", "Managua", 6850540L, "NIO", "America/Managua");
        add("Honduras", "HN", 15.2000, -86.2419, "Americas", "Tegucigalpa", 10278345L, "HNL", "America/Tegucigalpa");
        add("El Salvador", "SV", 13.7942, -88.8965, "Americas", "San Salvador", 6314167L, "USD", "America/El_Salvador");
        add("Guatemala", "GT", 15.7835, -90.2308, "Americas", "Guatemala City", 17608483L, "GTQ", "America/Guatemala");
        add("Belize", "BZ", 17.1899, -88.4976, "Americas", "Belmopan", 400031L, "BZD", "America/Belize");

        // ============================================
        // AFRICA
        // ============================================
        add("Nigeria", "NG", 9.0820, 8.6753, "Africa", "Abuja", 213401323L, "NGN", "Africa/Lagos");
        add("Egypt", "EG", 26.8206, 30.8025, "Africa", "Cairo", 109262178L, "EGP", "Africa/Cairo");
        add("South Africa", "ZA", -30.5595, 22.9375, "Africa", "Pretoria", 59392430L, "ZAR", "Africa/Johannesburg");
        add("Kenya", "KE", -0.0236, 37.9062, "Africa", "Nairobi", 53005614L, "KES", "Africa/Nairobi");
        add("Ethiopia", "ET", 9.1450, 40.4897, "Africa", "Addis Ababa", 120283026L, "ETB", "Africa/Addis_Ababa");
        add("Ghana", "GH", 7.9465, -1.0232, "Africa", "Accra", 32833031L, "GHS", "Africa/Accra");
        add("Tanzania", "TZ", -6.3690, 34.8888, "Africa", "Dodoma", 63585262L, "TZS", "Africa/Dar_es_Salaam");
        add("Algeria", "DZ", 28.0339, 1.6596, "Africa", "Algiers", 44177969L, "DZD", "Africa/Algiers");
        add("Morocco", "MA", 31.7917, -7.0926, "Africa", "Rabat", 37076584L, "MAD", "Africa/Casablanca");
        add("Tunisia", "TN", 33.8869, 9.5375, "Africa", "Tunis", 12262946L, "TND", "Africa/Tunis");
        add("Libya", "LY", 26.3351, 17.2283, "Africa", "Tripoli", 6735277L, "LYD", "Africa/Tripoli");
        add("Sudan", "SD", 12.8628, 30.2176, "Africa", "Khartoum", 45657202L, "SDG", "Africa/Khartoum");
        add("Uganda", "UG", 1.3733, 32.2903, "Africa", "Kampala", 45853778L, "UGX", "Africa/Kampala");
        add("Cameroon", "CM", 7.3697, 12.3547, "Africa", "Yaounde", 27198628L, "XAF", "Africa/Douala");
        add("Ivory Coast", "CI", 7.5400, -5.5471, "Africa", "Yamoussoukro", 27478249L, "XOF", "Africa/Abidjan");
        add("Senegal", "SN", 14.4974, -14.4524, "Africa", "Dakar", 16876720L, "XOF", "Africa/Dakar");
        add("Zimbabwe", "ZW", -19.0154, 29.1549, "Africa", "Harare", 15993524L, "ZWL", "Africa/Harare");
        add("Zambia", "ZM", -13.1339, 27.8493, "Africa", "Lusaka", 19473125L, "ZMW", "Africa/Lusaka");
        add("Mozambique", "MZ", -18.6657, 35.5296, "Africa", "Maputo", 32077072L, "MZN", "Africa/Maputo");
        add("Angola", "AO", -11.2027, 17.8739, "Africa", "Luanda", 34503774L, "AOA", "Africa/Luanda");
        add("Namibia", "NA", -22.9576, 18.4904, "Africa", "Windhoek", 2589178L, "NAD", "Africa/Windhoek");
        add("Botswana", "BW", -22.3285, 24.6849, "Africa", "Gaborone", 2351627L, "BWP", "Africa/Gaborone");
        add("Mali", "ML", 17.5707, -3.9962, "Africa", "Bamako", 21904983L, "XOF", "Africa/Bamako");
        add("Niger", "NE", 17.6078, 8.0817, "Africa", "Niamey", 25252722L, "XOF", "Africa/Niamey");
        add("Chad", "TD", 15.4542, 18.7322, "Africa", "N'Djamena", 17179740L, "XAF", "Africa/Ndjamena");
        add("Somalia", "SO", 5.1521, 46.1996, "Africa", "Mogadishu", 17065581L, "SOS", "Africa/Mogadishu");
        add("Eritrea", "ER", 15.1794, 39.7823, "Africa", "Asmara", 3620312L, "ERN", "Africa/Asmara");
        add("Djibouti", "DJ", 11.8251, 42.5903, "Africa", "Djibouti", 1105557L, "DJF", "Africa/Djibouti");
        add("Rwanda", "RW", -1.9403, 29.8739, "Africa", "Kigali", 13276947L, "RWF", "Africa/Kigali");
        add("Burundi", "BI", -3.3731, 29.9189, "Africa", "Gitega", 11890784L, "BIF", "Africa/Bujumbura");
        add("Malawi", "MW", -13.2543, 34.3015, "Africa", "Lilongwe", 20183367L, "MWK", "Africa/Blantyre");
        add("Madagascar", "MG", -18.7669, 46.8691, "Africa", "Antananarivo", 28915653L, "MGA", "Indian/Antananarivo");
        add("Mauritius", "MU", -20.3484, 57.5522, "Africa", "Port Louis", 1291162L, "MUR", "Indian/Mauritius");
        add("Seychelles", "SC", -4.6796, 55.4920, "Africa", "Victoria", 99202L, "SCR", "Indian/Mahe");

        // ============================================
        // OCEANIA
        // ============================================
        add("Australia", "AU", -25.2744, 133.7751, "Oceania", "Canberra", 25921089L, "AUD", "Australia/Sydney");
        add("New Zealand", "NZ", -40.9006, 174.8860, "Oceania", "Wellington", 5124100L, "NZD", "Pacific/Auckland");
        add("Fiji", "FJ", -17.7134, 178.0650, "Oceania", "Suva", 924610L, "FJD", "Pacific/Fiji");
        add("Papua New Guinea", "PG", -6.3150, 143.9555, "Oceania", "Port Moresby", 9949437L, "PGK", "Pacific/Port_Moresby");
        add("Samoa", "WS", -13.7590, -172.1046, "Oceania", "Apia", 218764L, "WST", "Pacific/Apia");
        add("Tonga", "TO", -21.1790, -175.1982, "Oceania", "Nuku'alofa", 106759L, "TOP", "Pacific/Tongatapu");
        add("Vanuatu", "VU", -15.3767, 166.9592, "Oceania", "Port Vila", 319137L, "VUV", "Pacific/Efate");
        add("Solomon Islands", "SB", -9.6457, 160.1562, "Oceania", "Honiara", 707851L, "SBD", "Pacific/Guadalcanal");

        // ============================================
        // CITY DATASET
        // ============================================
        cities("IN", city("Mumbai", 19.0760, 72.8777, "MEGA", "METRO"),
                city("Delhi", 28.6139, 77.2090, "MEGA", "CAPITAL"),
                city("Bengaluru", 12.9716, 77.5946, "MEGA", "TECH_HUB"),
                city("Hyderabad", 17.3850, 78.4867, "MEGA", "TECH_HUB"),
                city("Chennai", 13.0827, 80.2707, "MEGA", "METRO"),
                city("Kolkata", 22.5726, 88.3639, "MEGA", "METRO"),
                city("Pune", 18.5204, 73.8567, "LARGE", "TECH_HUB"),
                city("Ahmedabad", 23.0225, 72.5714, "LARGE", "METRO"));

        cities("US", city("New York", 40.7128, -74.0060, "MEGA", "METRO"),
                city("Los Angeles", 34.0522, -118.2437, "MEGA", "METRO"),
                city("Chicago", 41.8781, -87.6298, "MEGA", "METRO"),
                city("Houston", 29.7604, -95.3698, "LARGE", "METRO"),
                city("Phoenix", 33.4484, -112.0740, "LARGE", "METRO"),
                city("Washington", 38.9072, -77.0369, "LARGE", "CAPITAL"),
                city("San Francisco", 37.7749, -122.4194, "LARGE", "TECH_HUB"),
                city("Seattle", 47.6062, -122.3321, "LARGE", "TECH_HUB"));

        cities("CN", city("Beijing", 39.9042, 116.4074, "MEGA", "CAPITAL"),
                city("Shanghai", 31.2304, 121.4737, "MEGA", "METRO"),
                city("Guangzhou", 23.1291, 113.2644, "MEGA", "METRO"),
                city("Shenzhen", 22.5431, 114.0579, "MEGA", "TECH_HUB"),
                city("Chengdu", 30.5728, 104.0668, "MEGA", "METRO"),
                city("Hangzhou", 30.2741, 120.1551, "LARGE", "TECH_HUB"));

        cities("JP", city("Tokyo", 35.6762, 139.6503, "MEGA", "CAPITAL"),
                city("Osaka", 34.6937, 135.5023, "MEGA", "METRO"),
                city("Nagoya", 35.1815, 136.9066, "LARGE", "METRO"),
                city("Sapporo", 43.0618, 141.3545, "LARGE", "METRO"),
                city("Kyoto", 35.0116, 135.7681, "MEDIUM", "METRO"));

        cities("GB", city("London", 51.5074, -0.1278, "MEGA", "CAPITAL"),
                city("Manchester", 53.4808, -2.2426, "LARGE", "METRO"),
                city("Birmingham", 52.4862, -1.8904, "LARGE", "METRO"),
                city("Glasgow", 55.8642, -4.2518, "MEDIUM", "METRO"),
                city("Edinburgh", 55.9533, -3.1883, "MEDIUM", "CAPITAL"));

        cities("DE", city("Berlin", 52.5200, 13.4050, "MEGA", "CAPITAL"),
                city("Munich", 48.1351, 11.5820, "LARGE", "METRO"),
                city("Hamburg", 53.5511, 9.9937, "LARGE", "PORT"),
                city("Frankfurt", 50.1109, 8.6821, "LARGE", "TECH_HUB"),
                city("Cologne", 50.9375, 6.9603, "MEDIUM", "METRO"));

        cities("FR", city("Paris", 48.8566, 2.3522, "MEGA", "CAPITAL"),
                city("Marseille", 43.2965, 5.3698, "LARGE", "PORT"),
                city("Lyon", 45.7640, 4.8357, "LARGE", "METRO"),
                city("Toulouse", 43.6047, 1.4442, "MEDIUM", "METRO"),
                city("Nice", 43.7102, 7.2620, "MEDIUM", "METRO"));

        cities("AE", city("Dubai", 25.2048, 55.2708, "MEGA", "METRO"),
                city("Abu Dhabi", 24.4539, 54.3773, "LARGE", "CAPITAL"),
                city("Sharjah", 25.3463, 55.4209, "LARGE", "METRO"));

        cities("SG", city("Singapore", 1.3521, 103.8198, "MEGA", "CAPITAL"));

        cities("AU", city("Sydney", -33.8688, 151.2093, "MEGA", "METRO"),
                city("Melbourne", -37.8136, 144.9631, "MEGA", "METRO"),
                city("Brisbane", -27.4698, 153.0251, "LARGE", "METRO"),
                city("Perth", -31.9505, 115.8605, "LARGE", "METRO"),
                city("Canberra", -35.2809, 149.1300, "MEDIUM", "CAPITAL"));

        cities("BR", city("Sao Paulo", -23.5505, -46.6333, "MEGA", "METRO"),
                city("Rio de Janeiro", -22.9068, -43.1729, "MEGA", "METRO"),
                city("Brasilia", -15.8267, -47.9218, "LARGE", "CAPITAL"),
                city("Salvador", -12.9777, -38.5016, "LARGE", "METRO"));

        cities("CA", city("Toronto", 43.6532, -79.3832, "MEGA", "METRO"),
                city("Montreal", 45.5017, -73.5673, "LARGE", "METRO"),
                city("Vancouver", 49.2827, -123.1207, "LARGE", "PORT"),
                city("Ottawa", 45.4215, -75.6972, "MEDIUM", "CAPITAL"));

        cities("ZA", city("Johannesburg", -26.2041, 28.0473, "MEGA", "METRO"),
                city("Cape Town", -33.9249, 18.4241, "LARGE", "PORT"),
                city("Durban", -29.8587, 31.0218, "LARGE", "PORT"),
                city("Pretoria", -25.7479, 28.2293, "MEDIUM", "CAPITAL"));

        cities("EG", city("Cairo", 30.0444, 31.2357, "MEGA", "CAPITAL"),
                city("Alexandria", 31.2001, 29.9187, "LARGE", "PORT"),
                city("Giza", 30.0131, 31.2089, "LARGE", "METRO"));

        cities("NG", city("Lagos", 6.5244, 3.3792, "MEGA", "METRO"),
                city("Abuja", 9.0765, 7.3986, "LARGE", "CAPITAL"),
                city("Kano", 12.0022, 8.5920, "LARGE", "METRO"));

        cities("KE", city("Nairobi", -1.2921, 36.8219, "LARGE", "CAPITAL"),
                city("Mombasa", -4.0435, 39.6682, "LARGE", "PORT"));

        cities("RU", city("Moscow", 55.7558, 37.6173, "MEGA", "CAPITAL"),
                city("Saint Petersburg", 59.9311, 30.3609, "MEGA", "PORT"),
                city("Novosibirsk", 55.0084, 82.9357, "LARGE", "METRO"));

        cities("TR", city("Istanbul", 41.0082, 28.9784, "MEGA", "METRO"),
                city("Ankara", 39.9334, 32.8597, "LARGE", "CAPITAL"),
                city("Izmir", 38.4237, 27.1428, "LARGE", "PORT"));

        cities("SA", city("Riyadh", 24.7136, 46.6753, "MEGA", "CAPITAL"),
                city("Jeddah", 21.4858, 39.1925, "LARGE", "PORT"),
                city("Mecca", 21.3891, 39.8579, "LARGE", "METRO"));

        cities("KR", city("Seoul", 37.5665, 126.9780, "MEGA", "CAPITAL"),
                city("Busan", 35.1796, 129.0756, "LARGE", "PORT"),
                city("Incheon", 37.4563, 126.7052, "LARGE", "PORT"));

        cities("ID", city("Jakarta", -6.2088, 106.8456, "MEGA", "CAPITAL"),
                city("Surabaya", -7.2575, 112.7521, "LARGE", "PORT"),
                city("Bandung", -6.9175, 107.6191, "LARGE", "METRO"));

        cities("MX", city("Mexico City", 19.4326, -99.1332, "MEGA", "CAPITAL"),
                city("Guadalajara", 20.6597, -103.3496, "LARGE", "METRO"),
                city("Monterrey", 25.6866, -100.3161, "LARGE", "METRO"));

        cities("PK", city("Karachi", 24.8607, 67.0011, "MEGA", "PORT"),
                city("Lahore", 31.5204, 74.3587, "LARGE", "METRO"),
                city("Islamabad", 33.6844, 73.0479, "MEDIUM", "CAPITAL"));

        cities("IT", city("Rome", 41.9028, 12.4964, "MEGA", "CAPITAL"),
                city("Milan", 45.4642, 9.1900, "LARGE", "METRO"),
                city("Naples", 40.8518, 14.2681, "LARGE", "PORT"));

        cities("ES", city("Madrid", 40.4168, -3.7038, "MEGA", "CAPITAL"),
                city("Barcelona", 41.3851, 2.1734, "LARGE", "METRO"),
                city("Valencia", 39.4699, -0.3763, "LARGE", "PORT"));

        cities("TH", city("Bangkok", 13.7563, 100.5018, "MEGA", "CAPITAL"),
                city("Chiang Mai", 18.7883, 98.9853, "MEDIUM", "METRO"),
                city("Phuket", 7.8804, 98.3923, "MEDIUM", "PORT"));

        cities("MY", city("Kuala Lumpur", 3.1390, 101.6869, "MEGA", "CAPITAL"),
                city("George Town", 5.4141, 100.3288, "MEDIUM", "PORT"),
                city("Johor Bahru", 1.4927, 103.7414, "MEDIUM", "METRO"));

        cities("VN", city("Hanoi", 21.0285, 105.8542, "MEGA", "CAPITAL"),
                city("Ho Chi Minh City", 10.8231, 106.6297, "MEGA", "METRO"),
                city("Da Nang", 16.0544, 108.2022, "MEDIUM", "PORT"));

        // ============================================
        // REGIONAL TRAFFIC PROFILES
        // Honest labeling: these are ESTIMATED profiles
        // based on regional urban mobility patterns.
        // They are NOT real-time traffic observations.
        // ============================================
        traffic("IN", "HIGH", "08:00-11:00, 17:00-20:00", "INBOUND", "MEGA_METRO");
        traffic("US", "MODERATE", "07:00-09:30, 16:30-19:00", "INBOUND", "MEGA_METRO");
        traffic("CN", "HIGH", "07:30-09:30, 17:30-19:30", "INBOUND", "MEGA_METRO");
        traffic("JP", "MODERATE", "07:30-09:00, 17:30-19:30", "INBOUND", "MEGA_METRO");
        traffic("GB", "MODERATE", "07:30-09:30, 16:30-18:30", "INBOUND", "MEGA_METRO");
        traffic("DE", "MODERATE", "07:30-09:00, 16:30-18:30", "INBOUND", "MEGA_METRO");
        traffic("FR", "MODERATE", "07:30-09:30, 17:00-19:00", "INBOUND", "MEGA_METRO");
        traffic("AE", "MODERATE", "07:00-09:00, 17:00-19:00", "INBOUND", "MEGA_METRO");
        traffic("SG", "MODERATE", "07:30-09:00, 17:30-19:00", "INBOUND", "MEGA_METRO");
        traffic("AU", "LOW", "07:30-09:00, 17:00-18:30", "INBOUND", "MEGA_METRO");
        traffic("BR", "HIGH", "07:00-09:30, 17:00-20:00", "INBOUND", "MEGA_METRO");
        traffic("CA", "LOW", "07:30-09:00, 16:30-18:30", "INBOUND", "MEGA_METRO");
        traffic("ZA", "MODERATE", "06:30-08:30, 16:30-18:30", "INBOUND", "MEGA_METRO");
        traffic("EG", "HIGH", "07:00-09:30, 16:30-19:30", "INBOUND", "MEGA_METRO");
        traffic("NG", "SEVERE", "06:00-09:00, 16:00-20:00", "INBOUND", "MEGA_METRO");
        traffic("KE", "HIGH", "06:30-09:00, 16:30-19:30", "INBOUND", "LARGE_METRO");
        traffic("RU", "MODERATE", "07:30-09:30, 17:30-19:30", "INBOUND", "MEGA_METRO");
        traffic("TR", "HIGH", "07:00-09:30, 17:00-20:00", "INBOUND", "MEGA_METRO");
        traffic("SA", "MODERATE", "07:00-09:00, 16:30-19:00", "INBOUND", "MEGA_METRO");
        traffic("KR", "MODERATE", "07:30-09:00, 17:30-19:30", "INBOUND", "MEGA_METRO");
        traffic("ID", "SEVERE", "06:00-09:00, 16:00-20:00", "INBOUND", "MEGA_METRO");
        traffic("MX", "HIGH", "06:30-09:30, 17:00-20:00", "INBOUND", "MEGA_METRO");
        traffic("PK", "SEVERE", "07:00-09:30, 16:30-20:00", "INBOUND", "MEGA_METRO");
        traffic("IT", "MODERATE", "07:30-09:30, 17:00-19:30", "INBOUND", "MEGA_METRO");
        traffic("ES", "MODERATE", "07:30-09:30, 17:00-20:00", "INBOUND", "MEGA_METRO");
        traffic("TH", "SEVERE", "06:30-09:30, 16:00-20:00", "INBOUND", "MEGA_METRO");
        traffic("MY", "HIGH", "07:00-09:30, 17:00-19:30", "INBOUND", "MEGA_METRO");
        traffic("VN", "HIGH", "07:00-09:00, 17:00-19:30", "INBOUND", "MEGA_METRO");
    }

    // ============================================
    // REGISTRATION HELPERS
    // ============================================
    private static void add(
            String name,
            String code,
            double latitude,
            double longitude,
            String region,
            String capital,
            long population,
            String currency,
            String timezone) {

        COUNTRIES.put(code, new CountryData(
                name, code, latitude, longitude,
                region, capital, population, currency, timezone));
    }

    private static void cities(String countryCode, CityData... cityList) {
        COUNTRY_CITIES.put(
                countryCode,
                new ArrayList<>(List.of(cityList)));
    }

    private static CityData city(
            String name,
            double latitude,
            double longitude,
            String tier,
            String type) {

        return new CityData(name, latitude, longitude, tier, type);
    }

    private static void traffic(
            String countryCode,
            String riskLevel,
            String peakHours,
            String peakDirection,
            String urbanProfile) {

        COUNTRY_TRAFFIC.put(
                countryCode,
                new TrafficProfile(
                        riskLevel, peakHours, peakDirection, urbanProfile));
    }

    // ============================================
    // GET /api/global/countries
    // ============================================
    @GetMapping("/countries")
    public ResponseEntity<List<Map<String, Object>>> getCountries() {

        List<Map<String, Object>> result = new ArrayList<>();

        COUNTRIES.values().stream()
                .sorted(Comparator.comparing(CountryData::name))
                .forEach(country -> result.add(toMap(country)));

        return ResponseEntity.ok(result);
    }

    // ============================================
    // GET /api/global/countries/region/{region}
    // ============================================
    @GetMapping("/countries/region/{region}")
    public ResponseEntity<List<Map<String, Object>>> getCountriesByRegion(
            @PathVariable String region) {

        List<Map<String, Object>> result = new ArrayList<>();

        COUNTRIES.values().stream()
                .filter(country -> country.region().equalsIgnoreCase(region))
                .sorted(Comparator.comparing(CountryData::name))
                .forEach(country -> result.add(toMap(country)));

        return ResponseEntity.ok(result);
    }

    // ============================================
    // GET /api/global/countries/{code}
    // ============================================
    @GetMapping("/countries/{code}")
    public ResponseEntity<Map<String, Object>> getCountry(
            @PathVariable String code) {

        CountryData country = COUNTRIES.get(code.toUpperCase());

        if (country == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toMap(country));
    }

    // ============================================
    // GET /api/global/regions
    // ============================================
    @GetMapping("/regions")
    public ResponseEntity<List<String>> getRegions() {

        List<String> regions = COUNTRIES.values().stream()
                .map(CountryData::region)
                .distinct()
                .sorted()
                .toList();

        return ResponseEntity.ok(regions);
    }

    // ============================================
    // GET /api/global/countries/{code}/time
    // ============================================
    @GetMapping("/countries/{code}/time")
    public ResponseEntity<Map<String, Object>> getCountryTime(
            @PathVariable String code) {

        CountryData country = COUNTRIES.get(code.toUpperCase());

        if (country == null) {
            return ResponseEntity.notFound().build();
        }

        ZonedDateTime now;

        try {
            now = ZonedDateTime.now(ZoneId.of(country.timezone()));
        } catch (Exception exception) {
            now = ZonedDateTime.now(ZoneId.of("UTC"));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("country", country.name());
        result.put("code", country.code());
        result.put("timezone", country.timezone());
        result.put(
                "localTime",
                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        result.put(
                "localDate",
                now.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));

        return ResponseEntity.ok(result);
    }

    // ============================================
    // GET /api/global/countries/{code}/cities
    // ============================================
    @GetMapping("/countries/{code}/cities")
    public ResponseEntity<Map<String, Object>> getCountryCities(
            @PathVariable String code) {

        String countryCode = code.toUpperCase();
        CountryData country = COUNTRIES.get(countryCode);

        if (country == null) {
            return ResponseEntity.notFound().build();
        }

        List<CityData> cityList =
                COUNTRY_CITIES.getOrDefault(countryCode, List.of());

        List<Map<String, Object>> cities = new ArrayList<>();

        for (CityData city : cityList) {
            Map<String, Object> cityMap = new LinkedHashMap<>();
            cityMap.put("name", city.name());
            cityMap.put("latitude", city.latitude());
            cityMap.put("longitude", city.longitude());
            cityMap.put("tier", city.tier());
            cityMap.put("type", city.type());
            cities.add(cityMap);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("country", country.name());
        result.put("code", country.code());
        result.put("cityCount", cities.size());
        result.put("cities", cities);

        return ResponseEntity.ok(result);
    }

    // ============================================
    // GET /api/global/countries/{code}/traffic-summary
    // ============================================
    @GetMapping("/countries/{code}/traffic-summary")
    public ResponseEntity<Map<String, Object>> getCountryTrafficSummary(
            @PathVariable String code) {

        String countryCode = code.toUpperCase();
        CountryData country = COUNTRIES.get(countryCode);

        if (country == null) {
            return ResponseEntity.notFound().build();
        }

        List<CityData> cityList =
                COUNTRY_CITIES.getOrDefault(countryCode, List.of());

        int citiesTracked = cityList.size();

        TrafficProfile profile =
                COUNTRY_TRAFFIC.get(countryCode);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("country", country.name());
        result.put("code", country.code());
        result.put("region", country.region());

        if (profile == null) {
            // Honest label: no regional profile available for this country
            result.put("coverageStatus", "UNAVAILABLE");
            result.put("dataSource", "NO_REGIONAL_PROFILE");
            result.put("trafficRiskLevel", null);
            result.put("peakHours", null);
            result.put("peakDirection", null);
            result.put("urbanProfile", null);
            result.put("citiesTracked", citiesTracked);
            result.put(
                    "note",
                    "No regional traffic profile is currently available for this country.");
            return ResponseEntity.ok(result);
        }

        result.put("coverageStatus", "ESTIMATED");
        result.put("dataSource", "REGIONAL_PROFILE");
        result.put("trafficRiskLevel", profile.riskLevel());
        result.put("peakHours", profile.peakHours());
        result.put("peakDirection", profile.peakDirection());
        result.put("urbanProfile", profile.urbanProfile());
        result.put("citiesTracked", citiesTracked);
        result.put(
                "note",
                "Estimated regional profile. Not a real-time traffic observation.");

        return ResponseEntity.ok(result);
    }

    // ============================================
    // HELPER — Convert CountryData to Map
    // ============================================
    private Map<String, Object> toMap(CountryData country) {

        Map<String, Object> map = new LinkedHashMap<>();

        map.put("name", country.name());
        map.put("code", country.code());
        map.put("latitude", country.latitude());
        map.put("longitude", country.longitude());
        map.put("region", country.region());
        map.put("capital", country.capital());
        map.put("population", country.population());
        map.put("currency", country.currency());
        map.put("timezone", country.timezone());

        return map;
    }

    // ============================================
    // INNER RECORDS
    // ============================================
    private record CountryData(
            String name,
            String code,
            double latitude,
            double longitude,
            String region,
            String capital,
            long population,
            String currency,
            String timezone) {
    }

    private record CityData(
            String name,
            double latitude,
            double longitude,
            String tier,
            String type) {
    }

    private record TrafficProfile(
            String riskLevel,
            String peakHours,
            String peakDirection,
            String urbanProfile) {
    }
}