using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using System;
using System.Collections.Generic;
using System.Collections;

namespace MyStocksAnalysis {
    public partial class Checkout : ContentPage {
        private List<string> companies;
        private Label label;
        private double days;

        public Checkout(List<string> companies) {
            if (companies.Count < 1 || companies.Count > 2)
                throw new ArgumentException("Invalid number of companies.");
            this.companies = companies;
            this.days = 7;
            Title = "Select days";
            InitializeContent();
            Slider_ValueChanged(this, new ValueChangedEventArgs(0, 0));
        }

        private void InitializeContent() {
            List<ViewCell> viewCells = new List<ViewCell>();
            foreach (string companyName in this.companies) {
                Image i = new Image {
                    Source = App.companies[companyName],
                    Aspect = Aspect.AspectFill
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
            Slider slider = new Slider(min: 0, max: 23, val: 0);
            slider.ValueChanged += Slider_ValueChanged;
            Button button = new Button {
                Text = "Show graph"
            };
            button.Clicked += Button_Clicked;
            Content = new StackLayout {
                Children = {
                    tableView,
                    label,
                    slider,
                    button
                }
            };
        }

        private void Slider_ValueChanged(object sender, ValueChangedEventArgs e) {
            double value = e.NewValue + 7;
            this.days = Math.Floor(value);
            label.Text = "Number of days: " + this.days;
        }
        // to go back one step on the navigation stack
        // Navigation.PopAsync();

        private void Button_Clicked(object sender, EventArgs e) {
            Navigation.PushAsync(new Graph(this.companies, this.days));
        }
    }
}
