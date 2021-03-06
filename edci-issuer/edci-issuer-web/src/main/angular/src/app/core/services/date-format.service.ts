import { Injectable } from '@angular/core';
import * as moment from 'moment';
import { Constants } from '@shared/constants';

@Injectable({
    providedIn: 'root',
})
export class DateFormatService {
    constructor() {}

    /**
     * From a javascript Date returns a string date with the format "yy-MM-dd".
     * It does NOT take into account hours, minutes, seconds or timezone
     *
     * @param date Date (javascript Date object)
     * @returns string "yy-MM-dd"
     */
    dateToStringDate(date: Date): string {
        return date && moment(date).isValid()
            ? moment(date).format('YYYY-MM-DD')
            : null;
    }

    /**
     * From a javascript Date returns a string date with the format "yy-MM-ddTHH:mm:ss+HH:ss".
     * It takes into account hours, minutes, seconds or timezone
     *
     * @param date Date (javascript Date object)
     * @returns string "yy-MM-ddTHH:mm:ss+HH:ss"
     */
    dateToStringDateTime(date: Date): string {
        return date && moment(date).isValid()
            ? moment(date).format(Constants.LONG_DATE)
            : null;
    }

    /**
     * From a string date with the format "yy-MM-dd".
     * Returns a javascript Date.
     *
     * @param date string "yy-MM-dd"
     * @returns Date (javascript Date object)
     *
     */
    stringToDate(date: string): Date {
        return date ? moment(date, Constants.SHORT_DATE).toDate() : null;
    }

    /**
     * From a string date with the format "yy-MM-dd".
     * Returns a javascript Date.
     *
     * @param dateTime string "yy-MM-ddTHH:mm:ss+HH:ss"
     * @returns Date (javascript Date object)
     */
    stringTimeToDate(dateTime: string): Date {
        return dateTime ? moment(dateTime, Constants.LONG_DATE).toDate() : null;
    }

    /**
     * Checks that two dates are not invalid.
     * Invalid: initialDate < finalDate
     *
     * @param initialDate
     * @param finalDate
     */
    validateDates(initialDate: Date, finalDate: Date): boolean {
        let isValid: boolean = true;
        if (
            initialDate &&
            finalDate &&
            moment(initialDate).isAfter(finalDate)
        ) {
            isValid = false;
        }
        return isValid;
    }

    /**
     * Checks that two dates are not invalid.
     * Invalid: initialDate =< finalDate
     *
     * @param initialDate
     * @param finalDate
     */
    validateActivityDates(initialDate: Date, finalDate: Date): boolean {
        let isValid: boolean = true;
        if (
            initialDate &&
            finalDate &&
            !moment(initialDate).isSameOrBefore(finalDate)
        ) {
            isValid = false;
        }
        return isValid;
    }

    /**
     * Checks if value is a valid date.
     *
     * @param value
     */
    validateDate(value): boolean {
        return moment(value).isValid();
    }
}
