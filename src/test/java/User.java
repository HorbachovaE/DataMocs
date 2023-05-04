import com.thoughtworks.xstream.annotations.XStreamAlias;
@XStreamAlias("user")
public class User {

        private String userName;
        private int userId;

        public User(String userName, int userId) {
            this.userName = userName;
            this.userId = userId;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + userName + '\'' +
                    ", userId=" + userId +
                    '}';
        }

}
