using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using System;
using System.Collections.Generic;
using System.Collections;

namespace MyStocksAnalysis
{
    public partial class Checkout : ContentPage{
        private List<string> companies;
        private double days = 7;

    
        public Checkout(List<string> companies){
            this.companies = companies;
            InitializeComponent();
        }

        private void Slider_ValueChanged(object sender, ValueChangedEventArgs e)
        {
            double value = e.NewValue + 7;
            this.days = Math.Floor(value);
            label.Text = "Number of days " + this.days ;
            Console.WriteLine("show days" + this.days);
        }
        // to go back one step on the navigation stack
        // Navigation.PopAsync();

        private void ShowGraph_Clicked(object sender, EventArgs e)
        {
            Console.WriteLine("show days__" + this.days);

            Navigation.PushAsync(new Graph(this.companies, this.days));
        }
    }
}