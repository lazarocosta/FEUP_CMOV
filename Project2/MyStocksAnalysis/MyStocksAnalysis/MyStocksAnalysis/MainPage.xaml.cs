using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using System.Collections;

namespace MyStocksAnalysis {
    public partial class MainPage : ContentPage {
        public MainPage() {
            InitializeComponent();
        }

        private  void Button_Clicked(object sender, EventArgs e)
        {
            // to show OtherPage and be able to go back
            ArrayList companies = new ArrayList();
            companies.Add("Hello");
            Navigation.PushAsync(new Checkout(companies));
        }
    }
}
