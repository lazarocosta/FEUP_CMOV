using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using System.Collections;

namespace MyStocksAnalysis {
    public partial class MainPage : ContentPage {
        public List<string> itemsSelected;
        public MainPage() {
            this.itemsSelected = new List<string>();
            InitializeComponent();
            CompaniesSource.ItemsSource = new List<string>
            {
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

            CompaniesSource.ItemSelected += (object sender, SelectedItemChangedEventArgs e) =>
            {
                var item = e.SelectedItem;
                string itemString = item.ToString();

                if(this.itemsSelected.Contains(itemString))
                    this.itemsSelected.Remove(itemString);
                else
                    this.itemsSelected.Add(itemString);

                DisplayAlert("ItemSelected", this.itemsSelected.Contains(itemString).ToString(), "OK");
            };
        }

        private void ShowGraph_Clicked(object sender, EventArgs e)
        {
            Navigation.PushAsync(new Checkout(this.itemsSelected));
        }
    }

}
