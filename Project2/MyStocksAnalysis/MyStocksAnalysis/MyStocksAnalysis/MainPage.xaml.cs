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
                this.itemsSelected.Add(item.ToString());

                DisplayAlert("ItemSelected", this.itemsSelected.Contains(item.ToString()).ToString(), "OK");
            };
        }

        private void ShowGraph_Clicked(object sender, EventArgs e)
        {

            Navigation.PushAsync(new Checkout(this.itemsSelected));
        }
    }

}
