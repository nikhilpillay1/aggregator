import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {TableModule} from 'primeng/table';
import {FormsModule} from '@angular/forms';
import {Select} from 'primeng/select';
import {DatePicker} from 'primeng/datepicker';
import {InputText} from 'primeng/inputtext';
import {CurrencyPipe, NgForOf, NgIf} from '@angular/common';
import {FileUpload} from 'primeng/fileupload';
import {ButtonDirective} from 'primeng/button';
import {MessageService} from 'primeng/api';
import {ToastModule} from 'primeng/toast';

@Component({
  selector: 'app-home',
  imports: [
    TableModule,
    FormsModule,
    Select,
    DatePicker,
    InputText,
    CurrencyPipe,
    FileUpload,
    NgIf,
    NgForOf,
    ButtonDirective,
    ToastModule,
  ],
  providers: [MessageService],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {

  transactions: any[] = [];
  categories: any[] = [];
  sources: string[] = [];
  selectedSource: string | null = null;

  totalRecords = 0;
  loading = false;

  searchText = '';
  selectedCategory = null;
  dateFrom: Date | null = null;
  dateTo: Date | null = null;
  minAmount: number | null = null;
  maxAmount: number | null = null;

  private searchTimeout: any;
  private currentPage = 0;
  protected uploading: boolean = false;

  constructor(private http: HttpClient, private messageService: MessageService) {}

  ngOnInit() {
    this.loadTransactions({ first: 0, rows: 20 });
    this.loadSources();
    this.loadCategories();
  }

  private loadCategories() {
    this.http.get<any>('http://localhost:8080/api/transactions/categories').subscribe({
      next: (response) => {
        this.categories = response;
      },
    });
  }

  private loadSources() {
    this.http.get<any>('http://localhost:8080/api/transactions/sources').subscribe({
      next: (response) => {
        this.sources = response;
      },
    });
  }

  onSearch() {
    clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => {
      this.currentPage = 0;
      this.loadTransactions({ first: 0, rows: 20 });
    }, 300);
  }

  loadTransactions(event: any) {
    this.loading = true;
    this.currentPage = event.first / event.rows;

    let params = new HttpParams()
      .set('page', this.currentPage.toString())
      .set('size', event.rows.toString());

    if (this.searchText) params = params.set('description', this.searchText);
    if (this.selectedCategory) params = params.set('category', this.selectedCategory);
    if (this.dateFrom) params = params.set('dateFrom', this.formatDate(this.dateFrom));
    if (this.dateTo) params = params.set('dateTo', this.formatDate(this.dateTo));
    if (this.minAmount) params = params.set('minAmount', this.minAmount.toString());
    if (this.maxAmount) params = params.set('maxAmount', this.maxAmount.toString());

    if (event.sortField) {
      const sortDirection = event.sortOrder === 1 ? 'asc' : 'desc';
      params = params.set('sort', `${event.sortField},${sortDirection}`);
    }

    this.http.get<any>('http://localhost:8080/api/transactions/search', { params }).subscribe({
      next: (response) => {
        this.transactions = response.content;
        this.totalRecords = response.totalElements;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  onUpload(event: any) {
    this.uploading = true;

    const file = event.files[0];

    if (!this.selectedSource) {
      return;
    }

    const formData = new FormData();
    formData.append('file', file);
    formData.append('customerId', '1');
    formData.append('source', this.selectedSource as string);

    this.http.post<any[]>('http://localhost:8080/api/transactions/upload', formData)
      .subscribe({
        next: (transactions) => {
          this.loadTransactions({ first: 0, rows: 20 });
          event.files = [];
          this.uploading = false;

          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'Transactions uploaded successfully'
          });

        },
        error: (err) => {
          console.error('Error uploading file:', err);
          this.uploading = false;
          this.messageService.add({
            severity: 'error',
            summary: 'Upload Failed',
            detail: err.error?.message || 'An error occurred while uploading the file'
          });
        }
      });
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  deleteTransaction(id: number) {
      this.http.delete(`http://localhost:8080/api/transactions/${id}`).subscribe({
        next: () => {
          this.loadTransactions({ first: this.currentPage * 20, rows: 20 });
        },
        error: (err) => {
          console.error('Error deleting transaction:', err);
        }
      });
    }
}
