package ErrorInData

import Models.ErrorData

class ErrorMessage {

    private val list: Map<String, ErrorData>
//    private val defaultError = ErrorData(1, "Unknown error")

    init {
        list = mapOf<String, ErrorData>(
            ErrorEnum.Not.name to ErrorData(0, "Not Error"),
//            ErrorEnum.Not.name to ErrorData(0, "Unknown"),
            ErrorEnum.Parse.name to ErrorData(2, "Input data error"),
            ErrorEnum.AddressNotCorrect.name to ErrorData(3, "URL address not correct"),
            ErrorEnum.NoConfirmationRightsURL.name to ErrorData(4, "There is no confirmation of the rights to manage the URL address"),
            ErrorEnum.EntryExists.name to ErrorData(5, "The entry already exists"),
            ErrorEnum.Unknown.name to ErrorData(6, "Unknown error"),
            ErrorEnum.MethodNotFound.name to ErrorData(-32601, "Method not found"),
        )
    }

    fun get(error: ErrorEnum): ErrorData{
        return list.get(error.name )!!
//        return list.getOrDefault(error.name, list.get(ErrorEnum.Unknown)!! )
    }

}
