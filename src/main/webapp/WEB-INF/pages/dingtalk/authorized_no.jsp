<%@ page import="com.alibaba.fastjson.JSON,com.rkhd.ienterprise.apps.ingage.dingtalk.dto.*,com.rkhd.ienterprise.apps.ingage.dingtalk.enums.*" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="com.alibaba.fastjson.JSONObject" %>
<%@ page import="com.rkhd.ienterprise.apps.ingage.utils.ErrorCode" %><%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 2016/3/14
  Time: 16:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    //非异步请求
    if (!(request.getHeader("accept").indexOf("application/json") > -1 || (request
            .getHeader("X-Requested-With")!= null && request
            .getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1))) {
%>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5, user-scalable=0">
    <title>销售易CRM</title>
    <style type="text/css">
        .block{
            text-align: center;
            width: 100%;
            height: 300px;
            position: absolute;
            top: 50%;
            left: 50%;
            -webkit-transform: translate(-50%,-50%);
            transform: translate(-50%,-50%);
        }
        em{
            display: block;
            margin-bottom: 56px;
            width: 140px;
            height: 140px;
            background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIwAAACMCAYAAACuwEE+AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyhpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6QTY2NjVBNDlFNzMyMTFFNUFERTNDOTQ4MzIyM0IwMDgiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6QTY2NjVBNEFFNzMyMTFFNUFERTNDOTQ4MzIyM0IwMDgiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpBNjY2NUE0N0U3MzIxMUU1QURFM0M5NDgzMjIzQjAwOCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpBNjY2NUE0OEU3MzIxMUU1QURFM0M5NDgzMjIzQjAwOCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/Pl2nsYYAABE+SURBVHja7F1bd1XVFZ45OUm4JBEhCSAiNxEICA5BRZFWUNTW2lHa/gbfffW9Ptbn+hvq0FrrBUUdQhApaI0QwUiAckkIMVCCmHu6vp5vN4cYyFz7dtbeWd8YczCAvffZa65vrzXnXHPNVfWnP/9FZglqjNxtpIFSb2SekTlG6vhngVLDe0aMjFOGjAxSbhq5YWSAcpXXZg6vvPyS1fXFHJNjsZEWI01GFhppDPmcAHNnuPa6kX4jfUZ6jVzOKolmA2GqSI7lRu4lSQopv0MjZSX/Pk7yXDByniSa8ISpHECIZUZWs5PqHHy/FsrDnNLOGTlt5CIJ5QmTAhYZWW9kDe2OrACEfoAySOKcNPKDJ0z8qDZyv5FWI805mEJB9I2UK0Y6jHxvZMwTJvpX2UrFzsupgY4P4JdGHjFyguQZ8oSxQ62RBym1s8Ttn0fSbDHyDWXYE2bmqedBKq1OZifwgWw1ssnI1yTOmCfMzwFPZ7uEi5fkEfhgHqWBf9jIWU+YyfjFk1KKnySNYXol1ymI1v4ok1HcUbq7QcCthu5xUSYjwvOlFCUO4i6LEp428RvP0hU/wPeelYQpcNjdltB7jNMDQcS1R0pBtBuWzwiIM0Ri3Q4gEIKFS6QUYW6W+AOHiDn90chRI8elQnGcShEGazm7qdw48ZOUgmOIrF6Q9ELzNyhny0YmdPB9RlbIzMsKNv2FaXuVkY+ltI6Ve8KsNbIjxmEc00yXlAJhl8SN8PsIyQPBssVSKcWRVsfUbnxofzDSZqQzr4SpJlHWx/S8XsYsumh7uIoJEhlyiKRBbKklBm9qF8nYlpYnlRZhMMfvkehR2gl+te20TbIGEPs7CkaJzfQOqyI8cz0N7w9D2GhOEgZf0nMxzONnjByTUgpBHnCZnbyQcZdVEZ6FD3GvkQ848maWMCtp3Eb5nUuMQ/TlNN7ST+I00aC9J+Rz8EH+hsZwYjGbJHNGNnAaCkuWH9n4d3JMlnL0sa37I0wtRep8Q9ZGmM38WsLaKTBmj0gOM9YUgLf3bylFeVtD2De4fidd+/YsEAbJQttC3ou4widSCrTNZozQ8zlNT6ghxDO2s3+/dHlK2hKBLIgnvOHJcgt6qJOwsRb0xUOujjAYPh8Lcd8Yv6aTnh/TYpijbreU4ljVlvc/ymd0uESYFWxMGMN2n5TWfDzujCCdEwuR8y3v3UFdn3NhSkKc5ekQxhka/6YnixWuUGe2ecBV7KOWShMGEdznQ4xUWBh8W0obwjzscJO6uxBiNnmefVYRwlTT57fN3D9r5P1Z6jLH6UVBh2cs75vDPquuBGEwL9quDSEzHlHNcd/nkQEdfkSd2qA5pL0ZiTBIUbBddT5Da3/C93VsmKBObUea9ezDVAjTEIKhSGja78mSGGn2U8e2M0RD0oSBtY3FRJskoH4OnX4aSn56slnJRx/ufvW116uSJAy2gNikVcL3f88buKkZwu/JnXOPp2Ix+zQRwiB7fZsl6/dZNsAjGoJAqE323TYzyjQmQZidYhdvOSg+KFcJQOdtFtcX2bexEgbZYMssXgKLZX5tqHI4KXYLlsvMKLMqLsIgyGOT2zJgyXCPZNAmdttQthvSzBjQ00wxmyzcryAuEPcmcqQtruEoF4S2kZWG3YBd/DMLSLMdwSr3i6Jb52ugAfyvKCMMXC+bfAosoceZz7JASnmqkA00vIPChY38txf4/wscJkql2tEjdmkNW8woUxuFMEi11FZRwJdyJMbGYr/NXtElRd/Da5c6SJZKt+OI6HOE69jnoQhTx+lIi8MxxlvwlWFrSo3FPTW8Z4FjI0ul2zHCvtFikxll5oQhzEbRR3R7OAfHhack3JbSWt7rClxpR5eFqYDfbrUlTOFON02DQzE2DgZhlESfFssQQFJwrR02fdRqRpmCDWGwkqmtKYeV0jj3Da1x5Bl5a0ef6Fe10fcP2BBmo4UbfSwB19OFZ+SxHcdEnzHQqiUMEmyalA9FUnHce53rHXlGHtvRL/pE8CYzLTVrCLPO4gXaE1C087VqM94Omz5bNxNh8PfVyof1SjKbzuJY3b7hQMe42o4e0Vd4WD3V+C1MM2dqk7q/TUjRcaxwu1A7xuV2aPtuzlRPrRDSKsc6xemEGtPpyDPy3I7Tol/vW3M7wmCBaqXFDyZVJuyCRCuK0yv2e3ZmWztGLT74FeVpnOWEQaCozoIwSeJTCbfiPcx7XYHL7dD2ITixeDrCLFc+ADvvuhNuzDUpld+yWZsa4T3XHCKMy+3oFv3O03ujEAbFbtLYLoIGvakc1nt5bbe4B1fbMcG+1GD5VMIgkWqRBWHS/ELfMvKulNIOUTZ9nHPwNf7bu7zmmrgLV9uh7ctFxo4pBkQJ7BdNuiYaealCBuQFyT5ca8cl9ulMfR8cR3gpuHCJRWxhWDzygmHRx4uWlE9J2rWjy17HuYO2T5vKCbNQeZOvP5c/aPt0YUAYpARqd771ef3mDto+bTSGbw0Io80dxZlBN7x+84VXXn7phugPJb27YDG69Hv15hbavm0AYbSb1P7j9ZpbXEuCMH46yi8GbAijPZbmutdrbqEdDOaCMNqEqZ+8XnMLbd/OsSHMkNdrbjFoQ5jamFnokd8Rpi44xFsDX6cuv9BmT1aDMNoqir5kan6h7duqYGnAjzCzGK+8/JK2b2sKXl0eNihYjBw1Xl35BBYVtbNMwWb+8qrNLdR2bMHCQi56veYW2r4dA2G0KZdzvV5zC23fDoEw6iif12tuoe3bQRvC+BHGjzD/I4w2LNzg9ZpbaPv2JxBmwBPGE0Z53UDRgjB35UhBOPf5binlMy9g2/BvdfQYEJcYoweJVfoRTt3IOrxGuSr5OdrnriQIszDDCgERllNQZVtTO66aUl7RYmoZVCQeYR/0eUpWU0AW2hDmqoXS6yU7qZrobBzpspYdncQySD2fD8GWU2yDxSmvZyQjtfpefe31etGXebla5HCL9EvN7oGmDBAGNWZR8n6DhSLiAAh5HwUjDcqCHRf3D3PX7nodwCJlEOHrVxIG+2vPOmyXPESiVHpRtY7vgoMeUJnhK4ftHe2++h9EJkPC2P22UnHTYgcbXGTHPCTuLV8EJfhRVRvnELVLcqXewkLbp33lhNHur0WhX6R0ulLBAbbJTtFvxqskqbeROAfFnZIfNexTDXqkbOjuFd250rj+Hkc6AAd1v5ABspQD7/prI086MhpqnYFxcuT/F48Gc5QC9zkQM/id6M9DcBGtbEOlY1vavvzBGLyjMoVd5y1+pFK5MYij7JVsx4TKYx97RV9bMG5UWRDmfPnQHgDz6sNKtxXBr7RLl63nUB6XBwQ7rJseYhC9vUmXeLRMP3VscxAVXsj218bwDnjGc7Rr0j62eanojzi6MB1hLlNZmtjFmpQJs8XIYzE8B/GmTn4xKNU1U7bhKOVHubW0VxWNRYwOayPaUfgAfkG9f52iTrVV34ekrEpVOWEmGGNZp/yxz1NyETdHJAvahSLGcZ54O0EjEHKMsYxW6iXsdP0Yn9uegk6LFoQ5Z+yXiekIA3QpCVPLHzyVgnG4PUKnnmL8I+lCAj2Uo1KKB60LSZzt/Ag7UhhdtFPq6anDYTlw0LY2oWpDChb8jpD3BgWSP5N0q05c529qCzlPB7R5RcLvqe27QZly+HphGn9bezos6rYmFfmFu7k7xFeKr7PNyN+ksvX4+vgObSGmbbR5V4Iu92LRH17aZaaj8TsRBrA5B2lLQvPrnhBeCHJVUEn7hLixrXeC7/KW2FfvQtuflWSCezZ9dmo6C/1nQRrRF/tdkUBMZHuIZ17kNOBiHb5+vttFy/uQ4PV4ArEf7XTXZ0aXKxrC2IwyGD63xtige8XuvOzAUH9P3K5QPsx3tD0MfoOUnSQSA7ZaTPMdt4sBTIfvRJ/HgSSlphgag+H3yRBk2S+6dbBKY5zvakuauNadmthXGtwkB9SEGbd07Z6IKd5iEwBD4PBjyVYZkgm+s03QszEmW9FmeuuYauzORJhgSNIO8whcrY7QmHmMX9gYuPsyMrJM9zHuszSEt4g+jD8d0DdLLabP2w4WhRl88OOWxmrYCg9D/C3NaAE39UPJ9qkqw2yDxuUOvK2wCeY1Yhf8PG5Gl8EwhAHaLV4UycSPhmwUEqaPGPm7zBxoOyz5qEqONnwxwzXXqZMvJHxS+SOi2yURfLh3XJooKL4EmwWxVtHniE4HhNffuIOX1i3Jh83TBNpyu4jwSeoiyvoXgnQ2eUNfm9FlOAphgG9Ev3cpiFJGWfrHLoYDRt6f4qlh7m+TfAHTzcEpU/FNtv0ziVYmrpZ9oXWjB9jXEpUwY4qhsxzYdrkjBmXiPMK/lrmh30k+D8joK3Nhu9jmOM7V3GHpdX5hRpcZpz2tf4+GIFK5THn9Wk4fUZOCYHx9xOfl+TS4r+hqd8b0vHXUmRYXDVlU8SGb7LUDYreQBoY3x6SATsn3WQfXYyQLdG4TAB1l30rchEGjjlpcj62qWECbLx5pYT51Xm1xz1EzulxPgjCBAXzZsgG/El+BMw0UqWubD/SyxtCNQhhY859YWu9YIX1GKr99Nc+AbveI3So/+vCT8vTLJAgTTE0HLe9BsvTT4ku3JoEq6tZ2u8rBMHZh2K++M4QHtMoyLuChI8su0a9CBzgV1siOMk0giHbF8p77/fQU6zT0DHVqgyshZohYCIMgDxbQBi3vw9fwnDeEI6GGOrQdWQbZZ2OVIAyA4kLvi32iM+bbFyXakv1sxTzqztZmGWVfRSoIFcfUgMWz/WKfyIQMMOwtbvYcUKOZOrPNcJxgH/VGfYG4bIlzIedFxAx+K6V90x53xnrqKkwg9CD7KDLi3Mbwrdgn6wCISmJv8VIa0sOeG7cAq85IgX0g5P2HxW7rUGqEAdplstqSLbBYhvyNTyW+PdBZx2K6zWE3+/9TYt6rncRGqS9pYIXZE91Ig+4EGztbjw3ESI1MOSQ/hY1bHZYENvYnVTYrKP63I0SDcT3Kpq5ko7tmGVngKiPDvz7k/ROc2hPJTEyyzhpeGNlju0P+DhSGwNQlEqcv50Rp4qgcpYYgPlJsYzmb1EsmXZgPL/6OlIJMYY/PgQJ/L6Xq2qjFkresOywYbhX7INxU4FSaD+JwnStJGJHJ0ht7JFrMZRWnqbOc8rKegQeDdjPbFHV9DeF+RHATr9KeVulPNORt2jRRYi5VJM4qErGDNs5oRkgCfWNTGXZXtMT0zJO0WcbSakBaQIOQCd9N4kQtKthCeYKk+Z7Pdm3rLEiOGNP9JEttTM9FvOqQ3GYPdB4IE6CT08kuiacgUS1HrfWcxxHRRNHDCxV0y+EWI2EeVbRWSPzHH0J/SGRLPc+5UtWogx19cJ+3xfgec8vIM865HcrtoZeV1BxfTy9nCT8C2GpJpHBg6kVe9XGp0L7ySpYvH6fxCiN2p+i3sGhRYOcFxiUwRC8LG+EHKHD9B2WyPu+Y3Fqnt1om6/Xi9FWsFjdQ7qKXk8YxO9jmc0AqvHvChXr3UMA/6C08LsmeLVlHe2KpZAcg9efiyLFDLh0Xc5Z2x4McEepkdiPY1/6NS16ga+cLQTHYBXiCpNkUo1eRFYyQJO3i4Mp90eGv6ygVh5jFRsl/dt5Nfigd4vBho0XHlTjEEaedcYyNEk89PZfQR6J8Lxk4WLSYEaVCkacoTXSb12TYzsGHgJLsJyVji6rFjH6RSDk8RFccxFmRAfKAJAgqdtG4z2J9vkwSJgAUHhwwjvA7lgmQSY+6ts1S+Q1zWKK4QnLgHXslWxU/c0eYqZ1zmQJjGaH5JSQOpjAE15I+GxLxpH6OgCBKj+QwY7Ao+cRI2egTACRCOfYgSltPz2tOmWBUqi7TSxD5BSEHywRrVgNlclVmSTrpfwUYABKH+RyqmmThAAAAAElFTkSuQmCC") center no-repeat;
            background-size: contain;
            margin: 0 auto;
        }
        p{
            color:#858e99;
            font-size: 32px;
            text-align: center;
            margin-bottom: 20px;
        }

    </style>
</head>
<body>
<div class="block">
    <em></em>
    <p>您的id已经过期，请重新登录</p>
</div>
</body>
</html>
<%
    }else {
        //异步求求
        PrintWriter writer = response.getWriter();
        EntityReturnData returnData = new EntityReturnData();
        ErrorObj errorObj = new ErrorObj();
        errorObj.setKey("noauthorized");
        errorObj.setMsg("您已经退出");
        errorObj.setStatus(ErrorCode.NOAUTHORIZED.getErrorCode());
        returnData.setEntity(errorObj);
        writer.write(JSON.toJSONString(returnData));
        writer.flush();
    }
%>