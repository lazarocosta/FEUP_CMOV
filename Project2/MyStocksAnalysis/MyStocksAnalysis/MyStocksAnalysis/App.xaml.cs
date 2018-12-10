using System;
using System.Collections.Generic;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

[assembly: XamlCompilation(XamlCompilationOptions.Compile)]
namespace MyStocksAnalysis {
    public partial class App : Application {
        readonly public static Dictionary<string, string> companies = new Dictionary<string, string>
        {
            // These images are tagged for reuse.
            { "AMD", "https://farm9.staticflickr.com/8692/16980768497_421e5dba93_o_d.jpg" },
            { "Apple", "https://farm9.staticflickr.com/8692/16980768497_421e5dba93_o_d.jpg" },
            { "Facebook", "https://farm9.staticflickr.com/8692/16980768497_421e5dba93_o_d.jpg" },
            { "Google", "https://farm9.staticflickr.com/8692/16980768497_421e5dba93_o_d.jpg" },
            { "Hewlett Packard", "https://farm9.staticflickr.com/8692/16980768497_421e5dba93_o_d.jpg" },
            { "IBM", "https://farm9.staticflickr.com/8692/16980768497_421e5dba93_o_d.jpg" },
            { "Intel", "https://farm9.staticflickr.com/8692/16980768497_421e5dba93_o_d.jpg" },
            { "Microsoft", "https://farm9.staticflickr.com/8692/16980768497_421e5dba93_o_d.jpg" },
            { "Oracle", "https://farm9.staticflickr.com/8692/16980768497_421e5dba93_o_d.jpg" },
            { "Twitter", "https://farm9.staticflickr.com/8692/16980768497_421e5dba93_o_d.jpg" }
        };
        readonly public static Dictionary<string, string> companiesSymbols = new Dictionary<string, string>
        {
            { "AMD", "AMD" },
            { "Apple", "AAPL" },
            { "Facebook", "FB" },
            { "Google", "GOOG" },
            { "Hewlett Packard", "HPQ" },
            { "IBM", "IBM" },
            { "Intel", "INTC" },
            { "Microsoft", "MSFT" },
            { "Oracle", "ORCL" },
            { "Twitter", "TWTR" }
        };

        public App() {
            InitializeComponent();

            MainPage = new NavigationPage(new MainPage());
        }

        protected override void OnStart() {
            // Handle when your app starts
        }

        protected override void OnSleep() {
            // Handle when your app sleeps
        }

        protected override void OnResume() {
            // Handle when your app resumes
        }
    }
}
