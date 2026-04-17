import { MaskSensitivePipe } from './mask-sensitive.pipe';

describe('MaskSensitivePipe', () => {
  it('masks all but last four characters by default', () => {
    const pipe = new MaskSensitivePipe();
    expect(pipe.transform('1234567890')).toBe('******7890');
  });

  it('supports custom visible suffix length', () => {
    const pipe = new MaskSensitivePipe();
    expect(pipe.transform('ABCDEFG', 2)).toBe('*****FG');
  });
});
