package ErrorInData

import JsonRpc.ErrorData

class UtilError {

    private val errorMessage = ErrorMessage()

    fun getErrorData(error: ErrorEnum): ErrorData {

        val errorData = errorMessage.get(error)

        return ErrorData(errorData.code, errorData.message)

    }
}