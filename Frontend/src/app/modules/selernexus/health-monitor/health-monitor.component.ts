import { Component, OnInit, OnDestroy } from '@angular/core';
import { TransferV2Service } from '../../../core/services/sellernexus/transfer-v2.service';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-health-monitor',
  templateUrl: './health-monitor.component.html',
  styleUrls: ['./health-monitor.component.scss']
})
export class HealthMonitorComponent implements OnInit, OnDestroy {
  healthData: any = null;
  loading = false;
  errorMessage = '';
  private refreshSubscription?: Subscription;

  constructor(private transferV2Service: TransferV2Service) { }

  ngOnInit(): void {
    this.loadHealth();
    // Auto-refresh every 30 seconds
    this.refreshSubscription = interval(30000).subscribe(() => {
      this.loadHealth();
    });
  }

  ngOnDestroy(): void {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }
  }

  loadHealth(): void {
    this.loading = true;
    this.errorMessage = '';

    this.transferV2Service.getHealth().subscribe({
      next: (data) => {
        this.healthData = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading health data:', err);
        this.errorMessage = 'Failed to load health data';
        this.loading = false;
      }
    });
  }

  resetCircuitBreaker(serviceName: string): void {
    if (confirm(`Are you sure you want to reset the circuit breaker for ${serviceName}?`)) {
      this.transferV2Service.resetCircuitBreaker(serviceName).subscribe({
        next: () => {
          this.loadHealth();
        },
        error: (err) => {
          console.error('Error resetting circuit breaker:', err);
          this.errorMessage = 'Failed to reset circuit breaker';
        }
      });
    }
  }

  getCircuitBreakers(): any[] {
    if (!this.healthData?.circuitBreakers) return [];
    return Object.entries(this.healthData.circuitBreakers).map(([name, data]) => ({
      name,
      ...data as any
    }));
  }

  getStateClass(state: string): string {
    switch (state) {
      case 'CLOSED': return 'state-closed';
      case 'OPEN': return 'state-open';
      case 'HALF_OPEN': return 'state-half-open';
      default: return '';
    }
  }
}
