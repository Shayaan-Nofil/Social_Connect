import java.io.Serializable

class User: Serializable {
    var id: String = ""
    var name:String = ""
    var email: String = ""
    var number: String = ""
    var city: String = ""
    var  country: String = ""
    var token: String = ""
    var profilepic: String = "https://firebasestorage.googleapis.com/v0/b/smd-assignment-5b0a4.appspot.com/o/ali_circle.png?alt=media&token=5942b7cc-f38b-40f3-bc6a-82bb11142f0d"
    var bgpic: String = "https://firebasestorage.googleapis.com/v0/b/smd-assignment-5b0a4.appspot.com/o/ali_backdrop.jpg?alt=media&token=d7dda406-12ec-45ff-940e-5d9aed4f85c5"
    fun addData (ID:String = "", nam:String = "", eml: String = "" , num: String = "", cty: String = "" , ctr: String){
        name = nam
        email = eml
        number = num
        city = cty
        country = ctr
    }
}