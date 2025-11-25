import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JoomAuthService } from '../../../core/services/sellernexus/joom-auth.service';

interface JoomCredential {
  id: number;
  label: string;
  externalMerchantId: string;
  createdAt?: string;
}

@Component({
  selector: 'app-joom-login',
  templateUrl: './joom-login.component.html',
  styleUrls: ['./joom-login.component.scss']
})
export class JoomLoginComponent implements OnInit {

  message = '';
  clientId = '';
  labelEmail = '';
  clientSecret = '';

  credentials: JoomCredential[] = [];
  connecting = false;

  constructor(
    private joomAuth: JoomAuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Listen for query params from the backend redirect and show a friendly message
    this.route.queryParams.subscribe(params => {
      const status = params['status'];
      const reason = params['reason'];
      const label = params['label'];
      if (status === 'success') {
        this.message = label ? `✅ JOOM account "${label}" connected successfully.` : '✅ JOOM connected successfully.';
        // refresh credentials list to show newly added account
        this.loadCredentials();
      } else if (status === 'error') {
        this.message = `❌ JOOM connection failed${reason ? ': ' + reason : '.'}`;
      } else {
        // no status provided — clear message or keep default
        this.message = '';
      }
    });

    this.loadCredentials();
  }

  loadCredentials() {
    this.joomAuth.getCredentials().subscribe({
      next: (res: any[]) => {
        this.credentials = res || [];
      },
      error: (err) => {
        console.error('Failed to load credentials', err);
      }
    });
  }

  deleteCredential(id: number) {
    if (!confirm('Delete this JOOM credential? This cannot be undone.')) return;
    this.joomAuth.deleteCredential(id).subscribe({
      next: () => {
        this.message = 'Credential removed.';
        this.loadCredentials();
      },
      error: (err) => {
        console.error('Failed to delete credential', err);
        this.message = 'Failed to delete credential.';
      }
    });
  }

  connectJoom() {
    if (!this.clientId || !this.labelEmail) {
      this.message = 'Please enter client id and email (label).';
      return;
    }

    this.connecting = true;
    this.message = 'Starting authentication...';

    this.joomAuth.getAuthorizationUrl(this.clientId, this.labelEmail, this.clientSecret).subscribe({
      next: (url: string) => {
        window.location.href = url; // Redirect user to JOOM
      },
      error: () => {
        this.connecting = false;
        this.message = "❌ Failed to start Joom authentication.";
      }
    });
  }
}

