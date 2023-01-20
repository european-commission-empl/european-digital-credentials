package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import eu.europa.ec.empl.edci.datamodel.model.base.DownloadableAsset;

public interface DownloadableEntity extends DownloadableAsset {

    abstract Code getContentType();

    abstract void setContentType(Code contentType);

    abstract Code getContentEncoding();

    abstract void setContentEncoding(Code contentEncoding);
}
