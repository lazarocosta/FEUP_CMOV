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
        ArrayList companies;
        public Checkout(ArrayList companies){
            this.companies = companies;
            InitializeComponent();
        }

        private void Slider_ValueChanged(object sender, ValueChangedEventArgs e)
        {
            double value = e.NewValue + 7;
            label.Text = "Number of days " + Math.Floor(value);
        }
        // to go back one step on the navigation stack
        // Navigation.PopAsync();
    }
}