package de.metallist.backend;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * collection of reason codes which specify the result in a short way
 *
 * @author Metallist-dev
 * @version 0.1
 */
@AllArgsConstructor
public enum ReasonCodes {
    RC_GENERAL_SUCCESS("RC_GEN_00", "action successful"),
    RC_CREATE_SUCCESS("RC_ADD_00", "contract created"),
    RC_CREATE_ERROR("RC_ADD_10", "contract creation error"),
    RC_DELETE_SUCCESS("RC_DEL_00", "contract deleted"),
    RC_DELETE_MISSING("RC_DEL_10", "contract for deletion missing"),
    RC_DELETE_ERROR("RC_DEL_20", "general error while deletion"),
    RC_UPDATE_SUCCESS("RC_UPD_00", "contract updated"),
    RC_UPDATE_ERROR("RC_UPD_10", "contract update failed"),
    RC_GENERAL_ERROR("RC_GEN_99", "action failed");

    @Getter
    private final String codenumber;
    @Getter
    private final String description;

}
