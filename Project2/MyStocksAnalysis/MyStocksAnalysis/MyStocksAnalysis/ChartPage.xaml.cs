﻿using System.Linq;
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
        private PlotModel plotModel;

        public ChartPage(SortedSet<string> companies, int maxRecords) {
            InitializeComponent();
            Title = "Chart";
            Dictionary<string, Response> responses = new Dictionary<string, Response>();
            foreach(string companyName in companies)
                responses.Add(companyName, RestApi.POST(App.companiesSymbols[companyName], maxRecords));
            DrawChart(responses);
        }

        // Partially based on https://www.codeproject.com/Articles/1167724/Using-OxyPlot-with-Xamarin-Forms
        private void DrawChart(Dictionary<string, Response> responses) {
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
            plotModel = new PlotModel { Title = "Quotes on close" };
            plotModel.Axes.Add(new LinearAxis {
                Title = "Record number",
                Position = AxisPosition.Bottom,
                Minimum = 1,
                Maximum = maxNumResults
            });
            plotModel.Axes.Add(new LinearAxis {
                Title = "Quote on close",
                Position = AxisPosition.Left,
                Minimum = minClose,
                Maximum = maxClose
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
    }
}
