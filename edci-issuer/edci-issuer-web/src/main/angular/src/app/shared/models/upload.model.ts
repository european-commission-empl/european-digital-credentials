export class DocumentUpload {
    id: string;
    fileName?: string;
    date?: string;
    status?: string;
    visible?: boolean;
    action?: number;
    file?: File;

    constructor(id, filename, date, stat, visible?, action?, file?) {
        this.id = id;
        this.fileName = filename;
        this.date = date;
        this.status = stat;
        this.visible = visible || true;
        this.action = action || 0;
        this.file = file || null;
    }

}
