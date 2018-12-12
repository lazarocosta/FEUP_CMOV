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
    public partial class ChartPage : ContentPage {
        private SortedSet<string> companies;
        private int maxRecords;

        public ChartPage(SortedSet<string> companies) {
            this.companies = companies;
            InitializeComponent();
            Slider_ValueChanged(this, new ValueChangedEventArgs(0, 0));
            DrawChart(new Dictionary<string, Response>());
        }

        // Partially based on https://www.codeproject.com/Articles/1167724/Using-OxyPlot-with-Xamarin-Forms
        private void DrawChart(Dictionary<string, Response> responses) {
            bool emptyResponses = responses.Count <= 0;
            double minClose = double.MaxValue;
            double maxClose = double.MinValue;
            int maxNumResults = int.MinValue;
            foreach (Response response in responses.Values) {
                foreach (Response.Result result in response.results) {
                    double close = result.close;
                    if (minClose > close)
                        minClose = close;
                    if (maxClose < close)
                        maxClose = close;
                }
                int numResults = response.results.Count;
                if (maxNumResults < numResults)
                    maxNumResults = numResults;
            }
            PlotModel plotModel = new PlotModel { Title = "Quotes on close" };
            plotModel.Axes.Add(new LinearAxis {
                Title = "Record number",
                Position = AxisPosition.Bottom,
                Minimum = emptyResponses ? 0 : 1,
                Maximum = emptyResponses ? 1: maxNumResults
            });
            plotModel.Axes.Add(new LinearAxis {
                Title = "Quote on close",
                Position = AxisPosition.Left,
                Minimum = emptyResponses ? 0 : minClose,
                Maximum = emptyResponses ? 1 : maxClose
            });
            foreach (KeyValuePair<string, Response> pair in responses) {
                string companyName = pair.Key;
                Response response = pair.Value;
                LineSeries lineSeries = new LineSeries {
                    Title = companyName
                };
                ScatterSeries scatterSeries = new ScatterSeries {
                    MarkerType = MarkerType.Circle
                };
                int numResults = response.results.Count;
                for (int i = 0; i < numResults; i++) {
                    Response.Result result = response.results[i];
                    int x = i + 1;
                    double y = result.close;
                    lineSeries.Points.Add(new DataPoint(x, y));
                    scatterSeries.Points.Add(new ScatterPoint(x, y));
                }
                plotModel.Series.Add(lineSeries);
                plotModel.Series.Add(scatterSeries);
            }
            plotView.Model = plotModel;
        }

        private void Slider_ValueChanged(object sender, ValueChangedEventArgs e) {
            double value = e.NewValue + 7;
            this.maxRecords = (int)Math.Floor(value);
            label.Text = "Number of records: " + this.maxRecords;
        }

        private void Button_Clicked(object sender, EventArgs e) {
            try {
                Dictionary<string, Response> responses = new Dictionary<string, Response>();
                foreach (string companyName in companies)
                    responses.Add(companyName, RestApi.POST(App.companiesSymbols[companyName], maxRecords));
                DrawChart(responses);
            }
            catch (System.Net.WebException) {
                DisplayAlert("No Internet?", "Connection to server failed.", "OK");
            }
        }
    }
}
