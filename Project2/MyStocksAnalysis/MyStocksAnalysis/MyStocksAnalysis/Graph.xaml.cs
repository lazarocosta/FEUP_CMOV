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
        private string date;
        public PlotModel plotModel;

        public Graph(List<string> companies, int maxRecords) {
            InitializeComponent();
            Title = "Graph";
            ConvertCompanies(companies);
            RestApi restApi = new RestApi(companiesSymbols.ElementAt(0), maxRecords);
            Response result = restApi.POST();
            Console.WriteLine(result);
            DrawGraphic();
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
