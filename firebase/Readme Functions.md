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
Function to validTicket the icket
Parameters: 
        ticketId -> 
        userId ->
Output: JSON with result value 
{"data":"validated"}
or 
{"data":"was already used"}
or
{ "data": {error that caused the failure} }



--------------------------------------------------------
function buyTicket
Parameters
        date ->
        numbersTickets ->
        userId ->  
        priceTicket ->

output: 
{"data":"{ 
        'vouchersNumber': '1',
        'voucher': {  
                'id':'130e0690-d9e3-11e8-bdc7-e3808b3e4670',
                'productCode': 'popcorn'
        },
        'ticketsNumber': '1',
        'ticket': {
                'id':'130db870-d9e3-11e8-bdc7-e3808b3e4670',
                'date': '22-12-12'
        }"
}
or 
{ "data": {error that caused the failure} }

---------------------------------------
