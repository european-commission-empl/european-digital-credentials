import { Component, OnInit } from '@angular/core';
import { UxLink } from '@eui/core';
import { FooterMenuLink } from './footer-link.model';

@Component({
    selector: 'edci-footer',
    templateUrl: './footer.component.html',
    styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

    /* Main footer menu links */
    mainLinks: FooterMenuLink[] = [];
    /* Secondary footer menu links */
    secondaryLinks: FooterMenuLink[] = [];
    /* Other footer menu links */
    otherLinks: UxLink[] = [];

    constructor() {}

    ngOnInit() {
    // Create all links to the footer
        this.createMainLinks();
        this.createSecondaryLinks();
        this.createOtherLinks();
    }

    // Main links
    private createMainLinks() {
        this.mainLinks.push(
            {
                title: 'About Europass',
                links: [
                    new UxLink({
                        label: 'What is new?', url: '/home'
                    }),
                    new UxLink({
                        label: 'The history', url: '/home'
                    }),
                    new UxLink({
                        label: 'National Centres', url: '/home'
                    }),
                    new UxLink({
                        label: 'Become interoperable', url: '/home'
                    })]
            });
        this.mainLinks.push(
            {
                title: 'Digitally-Signed Credentials',
                links: [
                    new UxLink({
                        label: 'What is a digitally-sealed credential?', url: '/home'
                    }),
                    new UxLink({
                        label: 'How does it work?', url: '/home'
                    }),
                    new UxLink({
                        label: 'What are the adventages?', url: '/home'
                    }),
                    new UxLink({
                        label: 'European Digital Credentials Infrastructure', url: '/home'
                    })]
            });
        this.mainLinks.push(
            {
                title: 'Useful Links',
                links: [
                    new UxLink({
                        label: 'Skills panorama', url: '/home'
                    }),
                    new UxLink({
                        label: 'European SKills / Competences', url: '/home'
                    }),
                    new UxLink({
                        label: 'European QUalifications Framework', url: '/home'
                    }),
                    new UxLink({
                        label: 'Euroguidance Network', url: '/home'
                    })]
            });
    }

    private createSecondaryLinks() {
        this.secondaryLinks.push(
            {
                title: 'European Comission',
                links: [
                    new UxLink({
                        label: 'Comission and its priorities', url: '/home'
                    }),
                    new UxLink({
                        label: 'Policies information and services', url: '/home'
                    })]
            });
        this.secondaryLinks.push(
            {
                title: 'Follow the European Comission',
                links: [
                    new UxLink({
                        label: 'What is a digitally-sealed credential?', url: '/home'
                    })]
            });
        this.secondaryLinks.push(
            {
                title: 'European Union',
                links: [
                    new UxLink({
                        label: 'EU Institutions', url: '/home'
                    }),
                    new UxLink({
                        label: 'European Union', url: '/home'
                    })]
            });
    }

    private createOtherLinks() {
        this.otherLinks.push(
            new UxLink({
                label: 'Language policy', url: '/home'
            })
        );
        this.otherLinks.push(
            new UxLink({
                label: 'Privacy policy', url: '/home'
            })
        );
        this.otherLinks.push(
            new UxLink({
                label: 'Legal notice', url: '/home'
            })
        );
        this.otherLinks.push(
            new UxLink({
                label: 'Cookies', url: '/home'
            })
        );
        this.otherLinks.push(
            new UxLink({
                label: 'Web accessibility', url: '/home'
            })
        );
    }
}
