Function to register the customer
Parameters: 
	publicKey -> string
        name -> string
        nif -> number
        creditCardType -> string
        creditCardNumber -> number
        creditCardValidity -> date
Output: JSON with result value 
{"data":"121212ws1212"}
or
{ "data": {error that caused the failure} }

-----------------------------------------------------------------------------
function to payOrder
Parameters:
	userId->
	produsts-> {"product1":{"docProduct":"26QU3Rxbt3OdOyO8UP4X", "quantity":"2" }...}
	vouchers -> {"voucher":"e0593540-de9b-11e8-8169-cfeb6c1d7363",...} (opcional)
Output: JSON with result value 
{"data":"{ 'valueSpend': '2', 'voucher':['e0593540-de9b-11e8-8169-cfeb6c1d7363'],'number':'0008'}"}
or
{ "error": {error that caused the failure} }



----------------------------------------------
Function to validTicket the ticket
Parameters: 
        tickets -> {"ticketId1" : "d007fcb0-dddf-11e8-83c6-09fd741136c7", "ticketId2": "26feb680-dde0-11e8-83c6-09fd741136c7", ...}
        userId ->
Output: JSON with result value 
{"data":"validated"}
or 
{"data":[
			{
				"id":"d007fcb0-dddf-11e8-83c6-09fd741136c7",
				"state":"already been validaded"},
			{
				"id":"26feb680-dde0-11e8-83c6-09fd741136c7",
				"state":"validaded"
			}
		]
}

or
{ "error": {error that caused the failure} }

--------------------------------------------------------
function buyTickets
Parameters
        userId ->  
        tickets ->{"ticket1":{"id":"4YMjcrIXgaZmIzNH8BDF","numberTickets":"1"}...}

output: 
{"data":
	" 'vouchers': 
		[
			'voucher':{  
				'id':'734b5d70-dddd-11e8-978f-8b8c735d318b',
				'productCode': '5%discountCafeteria'
			},
			'voucher':{ 
				'id':'734d0b20-dddd-11e8-978f-8b8c735d318b',
				'productCode': 'freecoffee'
			}
		],
	'tickets': 
		[
			'ticket': {
				'id':'734ce410-dddd-11e8-978f-8b8c735d318b',
				'date': 'Mon Dec 12 2022 00:00:00 GMT+0000 (UTC)'
			}
		]
	"
}
or 
{ "error": {error that caused the failure} }


---------------------------------------
function listProducts
Parameters:

output:
{"data":[
			{"name":"popcorn","price":2},
			{"name":"coffee","price":1}
		]
}

--------------------------------------
function listVouchersUser
Parameters:
	userId

output:
{"data":
	[
		{"id":"44aa3030-e144-11e8-a6a5-a77c973b71e1","productCode":"freecoffee"},
		... ,
	]
}

--------------------------------------
function listTransactionsUser
Parameters:
	userId

output:
{"data":" 
	'vouchers': [
		{'id':'5eb625c0-e157-11e8-8655-17cfd52fdeef','productCode': 'freecoffee'},
		...
	],
	'tickets': [
		{'id':'51f51260-e157-11e8-8655-17cfd52fdeef','date': 'Wed Nov 21 2018 00:00:00 GMT+0000 (UTC)'},
		...
	],
	'product': [
		{'nameProduct':'coffee','priceProduct': '1','quantity': '2'},
		...
	]"
}

--------------------------------------
function listTicketsNotUsed
Parameters:
	userId

output:
{"data":" 
	'tickets': [
		{'id':'51f51260-e157-11e8-8655-17cfd52fdeef','date': 'Wed Nov 21 2018 00:00:00 GMT+0000 (UTC)'},
		...
	]
}


