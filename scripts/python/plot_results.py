import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os

def main():
    # Ścieżki do plików
    details_csv_path = 'outputs/details_runs.csv'
    output_dir = 'outputs/plots'
    
    # Tworzenie folderu na wykresy, jeśli nie istnieje
    os.makedirs(output_dir, exist_ok=True)
    
    if not os.path.exists(details_csv_path):
        print(f"Error: File {details_csv_path} not found. Ensure the Java experiment has been run.")
        return

    # Wczytanie danych
    df = pd.read_csv(details_csv_path)
    
    # Filtrowanie tylko dla instancji TSPA
    df_tspa = df[df['instance'] == 'TSPA'].copy()
    
    if df_tspa.empty:
        print("Brak danych dla instancji TSPA w pliku CSV.")
        return

    # Ustawienia stylu wykresów
    sns.set_theme(style="whitegrid")
    
    # ---------------------------------------------------------
    # Wykres 1: Funkcja Celu (Objective) - Boxplot
    # ---------------------------------------------------------
    plt.figure(figsize=(14, 8))
    
    # Sortowanie metod po średniej wartości funkcji celu malejąco (dla lepszej czytelności)
    order_obj = df_tspa.groupby('method')['objective'].mean().sort_values(ascending=False).index
    
    ax1 = sns.boxplot(
        data=df_tspa, 
        x='objective', 
        y='method', 
        order=order_obj,
        palette="viridis"
    )
    plt.title('Rozkład Wartości Funkcji Celu dla instancji TSPA (Wyżej = Lepiej)', fontsize=16)
    plt.xlabel('Funkcja Celu (Zysk - Koszt)', fontsize=14)
    plt.ylabel('Metoda', fontsize=14)
    plt.tight_layout()
    
    obj_plot_path = os.path.join(output_dir, 'TSPA_objective_boxplot.png')
    plt.savefig(obj_plot_path, dpi=300)
    print(f"Zapisano wykres funkcji celu w: {obj_plot_path}")
    plt.close()

    # ---------------------------------------------------------
    # Wykres 2: Czas Obliczeń (Time) - Boxplot
    # ---------------------------------------------------------
    plt.figure(figsize=(14, 8))
    
    # Sortowanie metod po średnim czasie rosnąco
    order_time = df_tspa.groupby('method')['timeMs'].mean().sort_values(ascending=True).index
    
    ax2 = sns.boxplot(
        data=df_tspa, 
        x='timeMs', 
        y='method', 
        order=order_time,
        palette="magma"
    )
    # Dodajemy skalę logarytmiczną, ponieważ różnice między Greedy a Steepest mogą być ogromne
    plt.xscale('log')
    plt.title('Czas Wykonania dla instancji TSPA (Skala Logarytmiczna) (Niżej = Szybciej)', fontsize=16)
    plt.xlabel('Czas [ms] (Log)', fontsize=14)
    plt.ylabel('Metoda', fontsize=14)
    plt.tight_layout()
    
    time_plot_path = os.path.join(output_dir, 'TSPA_time_boxplot.png')
    plt.savefig(time_plot_path, dpi=300)
    print(f"Zapisano wykres czasu obliczeń w: {time_plot_path}")
    plt.close()

if __name__ == '__main__':
    main()
