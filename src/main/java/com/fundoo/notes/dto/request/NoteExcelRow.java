
package com.fundoo.notes.dto.request;

/*
 * Class Name  : NoteExcelRow
 * Description : Holds one row of note data imported from Excel.
 * Author      : Ashish Srivastava
 * Date        : 16/11/2026
 */

public record NoteExcelRow(
        String title,
        String description
) {
}
