using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using System.Collections;

namespace MyStocksAnalysis {
    public partial class MainPage : ContentPage {
        readonly private static string switchId = "switchId";
        private SortedSet<string> itemsSelected;
        private Button button;

        public MainPage() {
            this.itemsSelected = new SortedSet<string>();
            InitializeComponent();
            Title = "Companies list";
            InitializeContent();
        }

        private void InitializeContent() {
            List<ViewCell> viewCells = new List<ViewCell>();
            foreach (KeyValuePair<string, ImageSource> pair in App.companiesImages) {
                string companyName = pair.Key;
                ImageSource companyImage = pair.Value;
               
                Image i = new Image {
                    Source = companyImage,
                    Aspect = Aspect.AspectFit,
                    WidthRequest = 100
                };
                Label l = new Label {
                    Text = companyName,
                    HorizontalOptions = LayoutOptions.FillAndExpand,
                    VerticalOptions = LayoutOptions.CenterAndExpand,
                    FontSize = 18
                };
                Switch s = new Switch();
                s.Toggled += SwitchToggledHandler;
                s.Resources.Add(switchId, companyName);
                StackLayout stackLayout = new StackLayout {
                    Orientation = StackOrientation.Horizontal,
                    HeightRequest = 30,
                    Children = { i, l, s }
                };
                viewCells.Add(new ViewCell() { View = stackLayout });
            }
            TableSection tableSection = new TableSection { viewCells };
            TableView tableView = new TableView {
                Root = new TableRoot { tableSection },
                Intent = TableIntent.Form
            };
            button = new Button {
                Text = "Next",
                IsEnabled = false
            };
            button.Clicked += Button_Clicked;
            Content = new StackLayout {
                Children = {
                    tableView,
                    button
                },
                Padding = new Thickness(4)
            };
        }

        private void SwitchToggledHandler(object sender, ToggledEventArgs e) {
            Switch s = (Switch)sender;
            string companyName = (string)s.Resources[switchId];
            if (e.Value) {
                this.itemsSelected.Add(companyName);
                if (this.itemsSelected.Count > 2) {
                    DisplayAlert("Too many options", "You can only select one or two options.", "OK");
                    s.IsToggled = false;    // Re-calls this handler.
                }
            }
            else
                this.itemsSelected.Remove(companyName);
            if (this.itemsSelected.Count >= 1 && this.itemsSelected.Count <= 2)
                button.IsEnabled = true;
            else
                button.IsEnabled = false;
        }

        private void Button_Clicked(object sender, EventArgs e) {
            Navigation.PushAsync(new CheckoutPage(this.itemsSelected));
        }
    }
}
