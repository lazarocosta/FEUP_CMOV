using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using System;
using System.Collections.Generic;

namespace MyStocksAnalysis
{
    public partial class Graph : ContentPage{
        private List<string> companiesInitials;
        private string date;

        public Graph(List<string> companies, double days)
        {
            InitializeComponent();
            this.ConvertCompanies(companies);
            this.GenerateDate(days);
            //var apirest = new ApiRest(companiesInitials.ElementAt(0), date);
            //apirest.GetService();
        }

        private void ConvertCompanies(List<string> companies)
        {
            this.companiesInitials = new List<string>();
            for (int i =0; i< companies.Count; i++)
            {
                switch (companies.ElementAt(i))
                {
                    case "AMD":
                        this.companiesInitials.Add("AMD");
                        break;
                    case "Apple":
                        this.companiesInitials.Add("AAPL");
                        break;
                    case "Facebook":
                        this.companiesInitials.Add("FB");
                        break;
                    case "Twitter":
                        this.companiesInitials.Add("TWTR");
                        break;
                    case "Oracle":
                        this.companiesInitials.Add("ORCL");
                        break;
                    case "Microsoft":
                        this.companiesInitials.Add("MSFT");
                        break;
                    case "Google":
                        this.companiesInitials.Add("GOOG");
                        break;
                    case "Hewlett Packard":
                        this.companiesInitials.Add("HPQ");
                        break;
                    case "Intel":
                        this.companiesInitials.Add("INTC");
                        break;
                    case "IBM":
                        this.companiesInitials.Add("IBM");
                        break;
                    default:
                        break;
                }
            }

        }

        private void GenerateDate(double days)
        {
            DateTime datenow = DateTime.Now;
            this.date = Convert.ToDateTime(datenow).Subtract(TimeSpan.FromDays(days)).ToString("yyyyMMdd");
        }
    }
}