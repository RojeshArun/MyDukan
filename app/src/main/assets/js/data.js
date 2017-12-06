var app=angular.module('sortApp',[])
app.controller('mainController', function($scope) {
	  $scope.sortType     = 'CompanyName' || 'City' || 'State' ||'Brand'; // set the default sort type
	  $scope.sortReverse  = false;  // set the default sort order
	  $scope.redmiServiceProviders   = '';     // set the default search/filter term
	
/*
	$http.get('assets/eventLists_one.json').success(function(data) {
		   $scope.jsonData = data;
		});
		*/
	  //create json Data to be set to the table//
 $scope.jsonData = [
	                                       {
                                               "CompanyName":"CELL WORLD",
                                               "City": "Abohar",
                                               "Brand":"Samsung",
                                               "Address":"Sathiya Saran Road",
                                               "PinCode":" 152116",
                                               "State":"Punjab",
                                               "PhoneNumber":"01634-225855"
                                             },
                                               {
                                                 "CompanyName":"S S CELL POINT",
                                                 "City": "Abohar",
                                                 "Brand":"Samsung",
                                                 "Address":" Shop No 7,8 & 9, Ganesh Theater Complex, 1st  Floor, Cinima Road",
                                                 "PinCode":" 504001",
                                                 "State":"Andhra Pradesh",
                                                 "PhoneNumber":"9059726534"
                                               },
                                               {
                                                 "CompanyName":"KRIPA COMMUNICATIONS",
                                                 "City": "Adimaly",
                                                 "Brand":"Samsung",
                                                 "Address":"Ground Floor, Landarc Shopping Complex, Centeral  Junction",
                                                 "PinCode":" 685561",
                                                 "State":"Kerala",
                                                 "PhoneNumber":"04864-22552"
                                               },

                                               {
                                                 "CompanyName":"S B ELECTRONICS",
                                                 "Address":"91, H G Basak Road, Melarmath",
                                                 "Brand":"Samsung",
                                                 "City": "Agartala",
                                                 "State":"Tripura",
                                                 "PinCode":" 799001",
                                                 "PhoneNumber":"0381-2380683"
                                               },

                                               {
                                                 "CompanyName":"ELECTRONIC PEOPLE",
                                                 "Address":"34, Lawyers Colony, New Subhash Nagar",
                                                 "City": "Agra",
                                                 "Brand":"Samsung",
                                                 "State":"Uttar Pradesh",
                                                 "PinCode":" 282002",
                                                 "PhoneNumber":"0562-4058808"
                                               },

                                               {
                                                 "CompanyName":"SHREE SHYAM ENTREPRISE",
                                                 "Address":"G-2, Nidhi House, Near Nidhi Co-operative Bank,  CTM, NH 8",
                                                 "City": "Ahmedabad",
                                                 "Brand":"Samsung",
                                                 "State":"Gujarat",
                                                 "PinCode":" 382415",
                                                 "PhoneNumber":"9737216938"
                                               },

                                               {
                                                 "CompanyName":"SHRI SAMARTH COMPUTERS & MOBILE",
                                                 "Address":"1st Floor, P N Gadgil Jwellers Building, Sarjepura",
                                                 "City": "Ahmednagar",
                                                 "Brand":"Samsung",
                                                 "State":"Maharashtra",
                                                 "PinCode":"  414001",
                                                 "PhoneNumber":"9270390652"
                                               },

                                               {
                                                 "CompanyName":"SHRI KRISHNA MOBILE SHOPEE",
                                                 "Address":"1st Floor, Prakash Reddy Complex, Thoadga Road Ahmedpur",
                                                 "City": "Ahmedpur",
                                                 "Brand":"Samsung",
                                                 "State":"Maharashtra",
                                                 "PinCode":" 413515",
                                                 "PhoneNumber":"9923234777"
                                               },

                                               {
                                                 "CompanyName":"LALDAILOVA PACHUAU AND SONS",
                                                 "Address":"Near Zodin Hyundai Show Room, Zarkawt",
                                                 "City": "Aizawl",
                                                 "Brand":"Samsung",
                                                 "State":"Mizoram",
                                                 "PinCode":" 796001",
                                                 "PhoneNumber":"0389-230023"
                                               },

                                               {
                                                 "CompanyName":"MOBILE CLINIC",
                                                 "Address":"F-7, First Floor, Amar Plaza, Opposite Daulat Bagh",
                                                 "City": "Ajmer",
                                                 "Brand":"Samsung",
                                                 "State":"Rajasthan",
                                                 "PinCode":" 305001",
                                                 "PhoneNumber":"982908711"
                                               },

                                               {
                                                 "CompanyName":"H N TELECOM",
                                                 "Address":"Ward No-6, Nehru Nagar Over Kiridge",
                                                 "City": "Akbarpur",
                                                 "Brand":"Samsung",
                                                 "State":"Uttar Pradesh",
                                                 "PinCode":"209101",
                                                 "PhoneNumber":"9956220733"
                                               },

                                               {
                                                 "CompanyName":"RAZA COMMUNICAITON",
                                                 "Address":"Sadubhau Chowk, Shankarnager Road",
                                                 "City": "Akluj",
                                                 "Brand":"Samsung",
                                                 "State":"Maharashtra",
                                                 "PinCode":" 425105",
                                                 "PhoneNumber":"02185-222686"
                                               },

                                               {
                                                 "CompanyName":"MOBILE TOUCH",
                                                 "Address":"2nd Floor, 13, Padiya Complex Tower Chowk, Station Road",
                                                 "City": "Akola",
                                                 "Brand":"Samsung",
                                                 "State":"Maharashtra",
                                                 "PinCode":"444001",
                                                 "PhoneNumber":"9823062462"
                                               },

                                               {
                                                 "CompanyName":"N N ENTERPRISES",
                                                 "Address":"20, Vivekanand Marg, Hotel Surya",
                                                 "City": "Allahabad",
                                                 "Brand":"Samsung",
                                                 "State":"Uttar Pradesh",
                                                 "PinCode":"211008",
                                                 "PhoneNumber":"7309228888"
                                               },

                                               {
                                                 "CompanyName":"KHULBE COMMUNICATION AND COMPUTER",
                                                 "Address":"1st Floor, Danpur Bhawan Mall Road",
                                                 "City": "Almora",
                                                 "Brand":"Samsung",
                                                 "State":"Uttarakhand",
                                                 "PinCode":"263601",
                                                 "PhoneNumber":"05962-232973"
                                               },

                                               {
                                                 "CompanyName":"WIRELESS WORLD",
                                                 "Address":"1st Floor, XIX/151, Palupallath Building, Opposite Federal Bank Head Office, Bank Junction",
                                                 "City": "Aluva",
                                                 "Brand":"Samsung",
                                                 "State":"Kerala",
                                                 "PinCode":"683101",
                                                 "PhoneNumber":"9946827676"
                                               },

                                               {
                                                 "CompanyName":"PAREEK ELECTRONICS",
                                                 "Address":"317, Scheme-2, Lajpat Nagar",
                                                 "City": "Alwar",
                                                 "Brand":"Samsung",
                                                 "State":"Rajasthan",
                                                 "PinCode":"301001",
                                                 "PhoneNumber":"-"
                                               },

                                               {
                                                 "CompanyName":"DEVICOM",
                                                 "Address":"10-397, Teja Towers Near, SKBR College, Konakapalli",
                                                 "City": "Amalapuram",
                                                 "Brand":"Samsung",
                                                 "State":"Andhra Pradesh",
                                                 "PinCode":"533201",
                                                 "PhoneNumber":"8856233123"
                                               },

                                               {
                                                 "CompanyName":"PADMAVATI MOBILE SHOPEE",
                                                 "Address":"Guruwar Peth, Opposite Wardhan Collection, Main Road",
                                                 "City": "Ambajogai",
                                                 "Brand":"Samsung",
                                                 "State":"Maharashtra",
                                                 "PinCode":"431517",
                                                 "PhoneNumber":"9766747444"
                                               },

                                               {
                                                 "CompanyName":"KOHLI TELECOM",
                                                 "Address":"114/6/13, In Front of Parkat Shiv Mandir, Near Mulak Raj Cycle",
                                                 "City": "Ambala Cantt",
                                                 "Brand":"Samsung",
                                                 "State":"Haryana",
                                                 "PinCode":"133001",
                                                 "PhoneNumber":"0171-4000683"
                                               },

                                               {
                                                 "CompanyName":"S K TECHNOLOGY",
                                                 "Address":"9C,1st Floor Ground Floor, Laxmi Sagar Appartment, Plot No 3-C, Shiv Mandir Road, Swami Samarth Chowk, Ambernath East",
                                                 "City": "Ambarnath",
                                                 "Brand":"Samsung",
                                                 "State":"Maharashtra",
                                                 "PinCode":"421501",
                                                 "PhoneNumber":"9323579598"
                                               },

                                               {
                                                 "CompanyName":"SUBHASINI",
                                                 "Address":"Krishna Complex, Vijay Marg In Front of Central Bank",
                                                 "City": "Ambikapur",
                                                 "Brand":"Samsung",
                                                 "State":"Chhattisgarh",
                                                 "PinCode":"497001",
                                                 "PhoneNumber":"07774-203777"
                                               },

                                               {
                                                 "CompanyName":"DIGITRONICS",
                                                 "Address":"1st Floor, Osmaniya Complex, Near Osmaniya Masjid, Bus Stand Road",
                                                 "City": "Amravati",
                                                 "Brand":"Samsung",
                                                 "State":"Maharashtra",
                                                 "PinCode":"444601",
                                                 "PhoneNumber":"0721-2578671"
                                               }

	           ];

	                 

});