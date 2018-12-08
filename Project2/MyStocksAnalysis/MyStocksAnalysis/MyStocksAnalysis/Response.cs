using System;
using System.Collections.Generic;
using System.Text;

namespace MyStocksAnalysis
{
    public class Response
    {
        public IList<string> Status { get; set; }
        public IList<Result> Results { get; set; }

        public class Result
        {
            public string symbol { get; set; }
            public DateTime timestamp { get; set; }

            public DateTime tradingDay { get; set; }
            public double open { get; set; }
            public double hight { get; set; }
            public double low { get; set; }
            public double close{ get; set; }
            public int volume { get; set; }
            public double openInterest { get; set; }
        }
    }
}
