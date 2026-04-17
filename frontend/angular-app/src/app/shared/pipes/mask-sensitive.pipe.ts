import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'maskSensitive',
  standalone: true
})
export class MaskSensitivePipe implements PipeTransform {
  transform(value: string | number | null | undefined, visibleSuffix = 4): string {
    const normalized = String(value ?? '');
    if (!normalized) {
      return '';
    }

    const maskedLength = Math.max(0, normalized.length - visibleSuffix);
    return `${'*'.repeat(maskedLength)}${normalized.slice(-visibleSuffix)}`;
  }
}
