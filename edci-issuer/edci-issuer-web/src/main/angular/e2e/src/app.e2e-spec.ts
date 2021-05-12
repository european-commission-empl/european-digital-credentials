import { AppPage } from './app.po';

describe('App', () => {
    let page: AppPage;

    beforeEach(() => {
        page = new AppPage();
    });

    it('should display header application studentName', () => {
        page.navigateTo();
        expect(page.getHeaderApplicationName()).toEqual('Application studentName');
    });
});
