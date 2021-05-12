export class Progress {
    batchStatus: string;
    exportDate: string;
    exportProgress: number;
    exportSummary: string;
    jobId: string;
    validationProgress: number;
    validationSummary?: string;
    duration?: string;

    constructor(status: string, exportProgress = 0) {
        this.batchStatus = status;
        this.exportProgress = exportProgress;
    }
}
