using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using System;
using System.Collections.Generic;
using OxyPlot;
using OxyPlot.Series;
using OxyPlot.Axes;

namespace MyStocksAnalysis {
    public partial class Graph : ContentPage {
        private List<string> companiesInitials;
        private string date;
        public PlotModel plotModel;

        public Graph(List<string> companies, double days) {
            InitializeComponent();
            ConvertCompanies(companies);
            GenerateDate(days);
            RestApi restApi = new RestApi(companiesInitials.ElementAt(0), date);
            restApi.POST();
            DrawGraphic();
        }

        private void ConvertCompanies(List<string> companies) {
            this.companiesInitials = new List<string>();
            for (int i = 0; i < companies.Count; i++) {
                switch (companies.ElementAt(i)) {
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

        private void GenerateDate(double days) {
            DateTime datenow = DateTime.Now;
            this.date = Convert.ToDateTime(datenow).Subtract(TimeSpan.FromDays(days)).ToString("yyyyMMdd");
        }

        // Partially based on https://www.codeproject.com/Articles/1167724/Using-OxyPlot-with-Xamarin-Forms
        private void DrawGraphic() {
            plotModel = new PlotModel { Title = "Plot model" };
            plotModel.Axes.Add(new LinearAxis {
                Position = AxisPosition.Bottom,
                Minimum = -20,
                Maximum = 80
            });
            plotModel.Axes.Add(new LinearAxis {
                Position = AxisPosition.Left,
                Minimum = -10,
                Maximum = 10
            });
            LineSeries lineSeries = new LineSeries();
            lineSeries.Points.Add(new DataPoint(0, 0));
            lineSeries.Points.Add(new DataPoint(2, 2));
            lineSeries.Points.Add(new DataPoint(20, 2));
            ScatterSeries scatterSeries = new ScatterSeries();
            scatterSeries.Points.Add(new ScatterPoint(0, 0));
            scatterSeries.Points.Add(new ScatterPoint(2, 2));
            scatterSeries.Points.Add(new ScatterPoint(20, 2));
            plotModel.Series.Add(lineSeries);
            plotModel.Series.Add(scatterSeries);
            plotView.Model = plotModel;
        }
    }
}
