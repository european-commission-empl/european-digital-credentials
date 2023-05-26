import { Pipe, PipeTransform } from '@angular/core';

/**
 * Convert bytes into largest possible unit.
 * Takes an precision argument that defaults to 2.
 * Usage:
 *   bytes | appFileSize:precision
 * @example
 *   {{ 1024 |  fileSize}}
 *   formats to: 1 KB
 */
@Pipe({
    name: 'appFileSize'
})
export class FileSizePipe implements PipeTransform {
    private units = ['bytes', 'KB', 'MB', 'GB', 'TB', 'PB'];

    transform(bytes = 0, precision = 2): string {
        if (isNaN(parseFloat(String(bytes))) || !isFinite(bytes)) {
            return '?';
        }

        let unit = 0;

        while (bytes >= 1024) {
            bytes /= 1024;
            unit++;
        }

        return bytes.toFixed(+precision) + ' ' + this.units[unit];
    }
}
