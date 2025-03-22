package nl.bvkatwijk.lens.processor;

import javax.lang.model.element.RecordComponentElement;

public record Field(String typeName, String fieldName, RecordComponentElement unused) {
}
