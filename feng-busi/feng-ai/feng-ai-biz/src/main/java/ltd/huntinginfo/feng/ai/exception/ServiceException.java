package ltd.huntinginfo.feng.ai.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 服务异常类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Getter
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = -1068765335343416833L;

    private final int code;

    public ServiceException(String message) {
        super(message);
        this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public ServiceException(int code, String message) {
        super(message);
        this.code = code;
    }
}
