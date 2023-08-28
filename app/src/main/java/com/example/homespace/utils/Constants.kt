val API_URL = "https://homespace-xrgv.onrender.com/graphql"

val countryCodes =  mapOf(
        "Afghanistan" to "AF",
        "Albania" to "AL",
        "Algeria" to "DZ",
        "American Samoa" to "AS",
        "Andorra" to "AD",
        "Angola" to "AO",
        "Anguilla" to "AI",
        "Antarctica" to "AQ",
        "Antigua and Barbuda" to "AG",
        "Argentina" to "AR",
        "Armenia" to "AM",
        "Aruba" to "AW",
        "Australia" to "AU",
        "Austria" to "AT",
        "Azerbaijan" to "AZ",
        "Bahamas (the)" to "BS",
        "Bahrain" to "BH",
        "Bangladesh" to "BD",
        "Barbados" to "BB",
        "Belarus" to "BY",
        "Belgium" to "BE",
        "Belize" to "BZ",
        "Benin" to "BJ",
        "Bermuda" to "BM",
        "Bhutan" to "BT",
        "Bolivia (Plurinational State of)" to "BO",
        "Bonaire, Sint Eustatius and Saba" to "BQ",
        "Bosnia and Herzegovina" to "BA",
        "Botswana" to "BW",
        "Bouvet Island" to "BV",
        "Brazil" to "BR",
        "British Indian Ocean Territory (the)" to "IO",
        "Brunei Darussalam" to "BN",
        "Bulgaria" to "BG",
        "Burkina Faso" to "BF",
        "Burundi" to "BI",
        "Cabo Verde" to "CV",
        "Cambodia" to "KH",
        "Cameroon" to "CM",
        "Canada" to "CA",
        "Cayman Islands (the)" to "KY",
        "Central African Republic (the)" to "CF",
        "Chad" to "TD",
        "Chile" to "CL",
        "China" to "CN",
        "Christmas Island" to "CX",
        "Cocos (Keeling) Islands (the)" to "CC",
        "Colombia" to "CO",
        "Comoros (the)" to "KM",
        "Congo (the Democratic Republic of the)" to "CD",
        "Congo (the)" to "CG",
        "Cook Islands (the)" to "CK",
        "Costa Rica" to "CR",
        "Croatia" to "HR",
        "Cuba" to "CU",
        "Curaçao" to "CW",
        "Cyprus" to "CY",
        "Czechia" to "CZ",
        "Czech Republic" to "CZ",
        "Côte d'Ivoire" to "CI",
        "Denmark" to "DK",
        "Djibouti" to "DJ",
        "Dominica" to "DM",
        "Dominican Republic (the)" to "DO",
        "Ecuador" to "EC",
        "Egypt" to "EG",
        "El Salvador" to "SV",
        "Equatorial Guinea" to "GQ",
        "Eritrea" to "ER",
        "Estonia" to "EE",
        "Eswatini" to "SZ",
        "Ethiopia" to "ET",
        "Falkland Islands (the) [Malvinas]" to "FK",
        "Faroe Islands (the)" to "FO",
        "Fiji" to "FJ",
        "Finland" to "FI",
        "France" to "FR",
        "French Guiana" to "GF",
        "French Polynesia" to "PF",
        "French Southern Territories (the)" to "TF",
        "Gabon" to "GA",
        "Gambia (the)" to "GM",
        "Georgia" to "GE",
        "Germany" to "DE",
        "Ghana" to "GH",
        "Gibraltar" to "GI",
        "Greece" to "GR",
        "Greenland" to "GL",
        "Grenada" to "GD",
        "Guadeloupe" to "GP",
        "Guam" to "GU",
        "Guatemala" to "GT",
        "Guernsey" to "GG",
        "Guinea" to "GN",
        "Guinea-Bissau" to "GW",
        "Guyana" to "GY",
        "Haiti" to "HT",
        "Heard Island and McDonald Islands" to "HM",
        "Holy See (the)" to "VA",
        "Honduras" to "HN",
        "Hong Kong" to "HK",
        "Hungary" to "HU",
        "Iceland" to "IS",
        "India" to "IN",
        "Indonesia" to "ID",
        "Iran (Islamic Republic of)" to "IR",
        "Iraq" to "IQ",
        "Ireland" to "IE",
        "Isle of Man" to "IM",
        "Israel" to "IL",
        "Italy" to "IT",
        "Jamaica" to "JM",
        "Japan" to "JP",
        "Jersey" to "JE",
        "Jordan" to "JO",
        "Kazakhstan" to "KZ",
        "Kenya" to "KE",
        "Kiribati" to "KI",
        "Korea (the Democratic People's Republic of)" to "KP",
        "Korea (the Republic of)" to "KR",
        "Kuwait" to "KW",
        "Kyrgyzstan" to "KG",
        "Lao People's Democratic Republic (the)" to "LA",
        "Latvia" to "LV",
        "Lebanon" to "LB",
        "Lesotho" to "LS",
        "Liberia" to "LR",
        "Libya" to "LY",
        "Liechtenstein" to "LI",
        "Lithuania" to "LT",
        "Luxembourg" to "LU",
        "Macao" to "MO",
        "Madagascar" to "MG",
        "Malawi" to "MW",
        "Malaysia" to "MY",
        "Maldives" to "MV",
        "Mali" to "ML",
        "Malta" to "MT",
        "Marshall Islands (the)" to "MH",
        "Martinique" to "MQ",
        "Mauritania" to "MR",
        "Mauritius" to "MU",
        "Mayotte" to "YT",
        "Mexico" to "MX",
        "Micronesia (Federated States of)" to "FM",
        "Moldova (the Republic of)" to "MD",
        "Monaco" to "MC",
        "Mongolia" to "MN",
        "Montenegro" to "ME",
        "Montserrat" to "MS",
        "Morocco" to "MA",
        "Mozambique" to "MZ",
        "Myanmar" to "MM",
        "Namibia" to "NA",
        "Nauru" to "NR",
        "Nepal" to "NP",
        "Netherlands (the)" to "NL",
        "New Caledonia" to "NC",
        "New Zealand" to "NZ",
        "Nicaragua" to "NI",
        "Niger (the)" to "NE",
        "Nigeria" to "NG",
        "Niue" to "NU",
        "Norfolk Island" to "NF",
        "Northern Mariana Islands (the)" to "MP",
        "Norway" to "NO",
        "Oman" to "OM",
        "Pakistan" to "PK",
        "Palau" to "PW",
        "Palestine, State of" to "PS",
        "Panama" to "PA",
        "Papua New Guinea" to "PG",
        "Paraguay" to "PY",
        "Peru" to "PE",
        "Philippines (the)" to "PH",
        "Pitcairn" to "PN",
        "Poland" to "PL",
        "Portugal" to "PT",
        "Puerto Rico" to "PR",
        "Qatar" to "QA",
        "Republic of North Macedonia" to "MK",
        "Romania" to "RO",
        "Russian Federation (the)" to "RU",
        "Rwanda" to "RW",
        "Réunion" to "RE",
        "Saint Barthélemy" to "BL",
        "Saint Helena, Ascension and Tristan da Cunha" to "SH",
        "Saint Kitts and Nevis" to "KN",
        "Saint Lucia" to "LC",
        "Saint Martin (French part)" to "MF",
        "Saint Pierre and Miquelon" to "PM",
        "Saint Vincent and the Grenadines" to "VC",
        "Samoa" to "WS",
        "San Marino" to "SM",
        "Sao Tome and Principe" to "ST",
        "Saudi Arabia" to "SA",
        "Senegal" to "SN",
        "Serbia" to "RS",
        "Seychelles" to "SC",
        "Sierra Leone" to "SL",
        "Singapore" to "SG",
        "Sint Maarten (Dutch part)" to "SX",
        "Slovakia" to "SK",
        "Slovenia" to "SI",
        "Solomon Islands" to "SB",
        "Somalia" to "SO",
        "South Africa" to "ZA",
        "South Georgia and the South Sandwich Islands" to "GS",
        "South Sudan" to "SS",
        "Spain" to "ES",
        "Sri Lanka" to "LK",
        "Sudan (the)" to "SD",
        "Suriname" to "SR",
        "Svalbard and Jan Mayen" to "SJ",
        "Sweden" to "SE",
        "Switzerland" to "CH",
        "Syrian Arab Republic" to "SY",
        "Taiwan" to "TW",
        "Tajikistan" to "TJ",
        "Tanzania, United Republic of" to "TZ",
        "Thailand" to "TH",
        "Timor-Leste" to "TL",
        "Togo" to "TG",
        "Tokelau" to "TK",
        "Tonga" to "TO",
        "Trinidad and Tobago" to "TT",
        "Tunisia" to "TN",
        "Turkey" to "TR",
        "Turkmenistan" to "TM",
        "Turks and Caicos Islands (the)" to "TC",
        "Tuvalu" to "TV",
        "Uganda" to "UG",
        "Ukraine" to "UA",
        "United Arab Emirates (the)" to "AE",
        "United Arab Emirates" to "AE",
        "United Kingdom of Great Britain and Northern Ireland (the)" to "GB",
        "United States Minor Outlying Islands (the)" to "UM",
        "United States of America (the)" to "US",
        "United States of America" to "US",
        "Uruguay" to "UY",
        "Uzbekistan" to "UZ",
        "Vanuatu" to "VU",
        "Venezuela (Bolivarian Republic of)" to "VE",
        "Viet Nam" to "VN",
        "Virgin Islands (British)" to "VG",
        "Virgin Islands (U.S.)" to "VI",
        "Wallis and Futuna" to "WF",
        "Western Sahara" to "EH",
        "Yemen" to "YE",
        "Zambia" to "ZM",
        "Zimbabwe" to "ZW",
        "Åland Islands" to "AX",
    )