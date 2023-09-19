export interface BatchStatusDTO {
    type: string;
    batchStatus: string;
    exportDate: string;
    exportProgress: number;
    exportSummary: string;
    jobId: string;
    validationProgress: number;
    validationSummary: string;
}
