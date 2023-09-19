
import { trigger, state, style, transition, animate } from '@angular/animations';

export const openCloseAnimation = trigger('openClose', [
    state('open', style({ height: '*', visibility: 'visible', overflow : 'hidden' })),
    state('close', style({ height: '0px', visibility: 'hidden', overflow: 'hidden' })),
    transition('close <=> open', [animate(416)])
]);
