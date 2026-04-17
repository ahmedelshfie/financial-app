export interface ReportItem {
  id: string;
  name: string;
  category: 'Compliance' | 'Finance' | 'Operations';
  lastGeneratedAt: string;
  status: 'Ready' | 'Running' | 'Failed';
}
