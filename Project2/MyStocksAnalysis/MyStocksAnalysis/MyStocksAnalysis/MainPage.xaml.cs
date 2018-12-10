using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using System.Collections;

namespace MyStocksAnalysis {
    public partial class MainPage : ContentPage {
        readonly private static List<string> companies = new List<string>() {
            "AMD",
            "Apple",
            "Facebook",
            "Google",
            "Hewlett Packard",
            "IBM",
            "Intel",
            "Microsoft",
            "Oracle",
            "Twitter"
        };
        private List<string> itemsSelected;
        private Button button;

        public MainPage() {
            this.itemsSelected = new List<string>();
            Title = "Companies list";
            InitializeContent();
        }

        private void InitializeContent() {
            List<Cell> cells = new List<Cell>();
            foreach (string c in companies) {
                SwitchCell switchCell = new SwitchCell { Text = c };
                switchCell.OnChanged += SwitchCellOnChangedHandler;
                cells.Add(switchCell);
            }
            TableSection tableSection = new TableSection { cells };
            TableView tableView = new TableView {
                Root = new TableRoot("Companies") { tableSection },
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

        private void SwitchCellOnChangedHandler(object sender, ToggledEventArgs e) {
            SwitchCell switchCell = (SwitchCell)sender;
            string itemString = switchCell.Text;
            if (e.Value) {
                if (this.itemsSelected.Count >= 2)
                    DisplayAlert("Too many options", "You can only select one or two options.", "OK");
                this.itemsSelected.Add(itemString);
            }
            else
                this.itemsSelected.Remove(itemString);
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
