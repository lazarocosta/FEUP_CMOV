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
        private List<string> itemsSelected;
        private Button button;

        public MainPage() {
            this.itemsSelected = new List<string>();
            Title = "Companies list";
            InitializeContent();
        }

        private void InitializeContent() {
            List<ViewCell> viewCells = new List<ViewCell>();
            foreach (KeyValuePair<string, string> pair in App.companies) {
                string companyName = pair.Key;
                string companyImage = pair.Value;
                Image i = new Image {
                    Source = companyImage,
                    Aspect = Aspect.AspectFill
                };
                Label l = new Label {
                    Text = companyName,
                    HorizontalOptions = LayoutOptions.FillAndExpand,
                    VerticalOptions = LayoutOptions.CenterAndExpand,
                    FontSize = 22
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
                }
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
            Navigation.PushAsync(new Checkout(this.itemsSelected));
        }
    }
}
