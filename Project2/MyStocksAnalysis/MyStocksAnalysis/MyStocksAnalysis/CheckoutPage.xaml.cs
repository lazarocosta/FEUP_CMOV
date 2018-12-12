using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using System;
using System.Collections.Generic;
using System.Collections;

namespace MyStocksAnalysis {
    public partial class CheckoutPage : ContentPage {
        private SortedSet<string> companies;
        private Label label;
        private int maxRecords;

        public CheckoutPage(SortedSet<string> companies) {
            if (companies.Count < 1 || companies.Count > 2)
                throw new ArgumentException("Invalid number of companies.");
            this.companies = companies;
            InitializeComponent();
            Title = "Select parameters";
            InitializeContent();
            Slider_ValueChanged(this, new ValueChangedEventArgs(0, 0));
        }

        private void InitializeContent() {
            List<ViewCell> viewCells = new List<ViewCell>();
            foreach (string companyName in this.companies) {
                Image i = new Image {
                    Source = App.companiesImages[companyName],
                    Aspect = Aspect.AspectFit,
                    WidthRequest = 100
                };
                Label l = new Label {
                    Text = companyName,
                    HorizontalOptions = LayoutOptions.FillAndExpand,
                    VerticalOptions = LayoutOptions.CenterAndExpand,
                    FontSize = 22
                };
                StackLayout stackLayout = new StackLayout {
                    Orientation = StackOrientation.Horizontal,
                    HeightRequest = 30,
                    Children = { i, l }
                };
                viewCells.Add(new ViewCell() { View = stackLayout });
            }
            TableSection tableSection = new TableSection { viewCells };
            TableView tableView = new TableView {
                Root = new TableRoot { tableSection },
                Intent = TableIntent.Data
            };
            label = new Label {
                HorizontalTextAlignment = TextAlignment.Center,
                VerticalTextAlignment = TextAlignment.Center
            };
            Slider slider = new Slider(min: 0, max: 23, val: 0) {
                ThumbColor = Color.DarkCyan,
                MinimumTrackColor = Color.Gray,
                MaximumTrackColor = Color.Black
            };
            slider.ValueChanged += Slider_ValueChanged;
            Button button = new Button {
                Text = "Show chart"
            };
            button.Clicked += Button_Clicked;
            Content = new StackLayout {
                Children = {
                    tableView,
                    label,
                    slider,
                    button
                },
                Padding = new Thickness(4)
            };
        }

        private void Slider_ValueChanged(object sender, ValueChangedEventArgs e) {
            double value = e.NewValue + 7;
            this.maxRecords = (int)Math.Floor(value);
            label.Text = "Number of records: " + this.maxRecords;
        }
        // to go back one step on the navigation stack
        // Navigation.PopAsync();

        private void Button_Clicked(object sender, EventArgs e) {
            try {
                Navigation.PushAsync(new ChartPage(this.companies, this.maxRecords));
            }
            catch (System.Net.WebException) {
                DisplayAlert("No Internet?", "Connection to server failed.", "OK");
            }
        }
    }
}
