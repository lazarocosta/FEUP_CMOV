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
function buyTicket
Parameters
        date ->
        numbersTickets ->
        userId ->  
        priceTicket ->

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
