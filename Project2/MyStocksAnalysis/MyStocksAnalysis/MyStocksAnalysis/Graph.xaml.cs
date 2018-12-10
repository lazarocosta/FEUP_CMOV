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
        private List<string> companiesSymbols;
        public PlotModel plotModel;

        public Graph(List<string> companies, int maxRecords) {
            InitializeComponent();
            Title = "Graph";
            ConvertCompanies(companies);
            Response response = RestApi.POST(companiesSymbols.ElementAt(0), maxRecords);
            DrawGraphic(response);
        }

        private void ConvertCompanies(List<string> companies) {
            this.companiesSymbols = new List<string>();
            for (int i = 0; i < companies.Count; i++) {
                switch (companies.ElementAt(i)) {
                    case "AMD":
                        this.companiesSymbols.Add("AMD");
                        break;
                    case "Apple":
                        this.companiesSymbols.Add("AAPL");
                        break;
                    case "Facebook":
                        this.companiesSymbols.Add("FB");
                        break;
                    case "Twitter":
                        this.companiesSymbols.Add("TWTR");
                        break;
                    case "Oracle":
                        this.companiesSymbols.Add("ORCL");
                        break;
                    case "Microsoft":
                        this.companiesSymbols.Add("MSFT");
                        break;
                    case "Google":
                        this.companiesSymbols.Add("GOOG");
                        break;
                    case "Hewlett Packard":
                        this.companiesSymbols.Add("HPQ");
                        break;
                    case "Intel":
                        this.companiesSymbols.Add("INTC");
                        break;
                    case "IBM":
                        this.companiesSymbols.Add("IBM");
                        break;
                    default:
                        throw new ArgumentException("Invalid company.");
                }
            }
        }

        // Partially based on https://www.codeproject.com/Articles/1167724/Using-OxyPlot-with-Xamarin-Forms
        private void DrawGraphic(Response response) {
            double minClose = Double.MaxValue;
            double maxClose = Double.MinValue;
            foreach (Response.Result result in response.results) {
                double close = result.close;
                if (minClose > close)
                    minClose = close;
                if (maxClose < close)
                    maxClose = close;
            }
            int numResults = response.results.Count;
            plotModel = new PlotModel { Title = "Plot model" };
            plotModel.Axes.Add(new LinearAxis {
                Position = AxisPosition.Bottom,
                Minimum = 0,
                Maximum = numResults + 1
            });
            plotModel.Axes.Add(new LinearAxis {
                Position = AxisPosition.Left,
                Minimum = minClose,
                Maximum = maxClose
            });
            LineSeries lineSeries = new LineSeries();
            ScatterSeries scatterSeries = new ScatterSeries();
            for (int i = 0; i < numResults; i++) {
                Response.Result result = response.results[i];
                int x = i + 1;
                double y = result.close;
                lineSeries.Points.Add(new DataPoint(x, y));
                scatterSeries.Points.Add(new ScatterPoint(x, y));
            }
            plotModel.Series.Add(lineSeries);
            plotModel.Series.Add(scatterSeries);
            plotView.Model = plotModel;
        }
    }
}
