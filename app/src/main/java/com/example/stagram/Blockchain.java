package com.example.stagram;

import android.util.Log;

import com.klaytn.caver.Caver;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.kct.kip17.KIP17;
import com.klaytn.caver.kct.kip7.KIP7;
import com.klaytn.caver.methods.response.Bytes32;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.transaction.AbstractTransaction;
import com.klaytn.caver.transaction.TxPropertyBuilder;
import com.klaytn.caver.transaction.type.ValueTransferMemo;
import com.klaytn.caver.utils.Utils;
import com.klaytn.caver.wallet.keyring.SingleKeyring;

import org.web3j.crypto.CipherException;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Blockchain {
    String address = "0x81fe9f0b45dee0c6ca17c2d6d492c0491a306261";
    String destination = "0xd045f3E738fFE5E6df6acabB561D0505f666ca76";
    String private_key = "0xc4d9ef7e10b3c48677ea1e5569a6ba2edc29264206e12cc6117bdc6c180aae1f";
    String BAO = "https://api.baobab.klaytn.net:8651/";
    String BAO_API = "https://public-node-api.klaytnapi.com/v1/baobab";
    String contract_address = "0xd4f27d65ba5186763584ea9ae6457bb587cfb485";
    String temp_contract_address = "0x934eee0a648bf3fbab3401c1d94a96bd36d8e4b2";
    String temp_private_key = "0xebcb16314b1c1b566d448ad1a705acb29954bf4c5e4528202c5716e13f140795";
    String contract_address_token = "0xede0d4ce2a168f9e3328455e758991d4599c356c";
    Caver caver = new Caver(BAO_API);
    boolean result=false;
    //0x103fd0b6e38d9d6364e491a0a4816d395571fd5cb4250557b4d7bc4d87ec9089
    //0x52099f88b66315a677fff3f50b7d5f8a8a9ec8e8
    //0x2089fdf833350238a0628da5c6190dff844d92b5e0236cb0852cd0dae64477b8
    //0xf91e6b72193e50f89197d66371700cb6649c29f6
    //0x9245f7fb946a501f6457c0fcee517c815e12202859bfd4028784eff048a9c913
    //0x15905b19369954027e82b1738b0b3df20c44698d0cc1ee114b2614669c78cdd5
    //0xaedd96e60c398fa7a9075b2ec2dc73d4febf874c

    String contractAddress;
    boolean confirm;

    public SingleKeyring getNewKeyring(){
        caver = new Caver(BAO_API);
        return caver.wallet.keyring.generate();
    }

    public String contract(String creator_private_key, String name, String symbol) throws InterruptedException{
        caver = new Caver(BAO_API);
        SingleKeyring keyring = (SingleKeyring) caver.wallet.keyring.createFromPrivateKey(creator_private_key);
        caver.wallet.add(keyring);
        new Thread(() -> {
            try {
                KIP17 kip=caver.kct.kip17.deploy(keyring.getAddress(),name, symbol);
                contractAddress=kip.getContractAddress();
                Log.e("contract주소1",contractAddress);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (TransactionException e) {
                e.printStackTrace();
            }
        }).start();
        while(contractAddress==null)
        {
            Thread.sleep(1000);
            Log.e("타이머반복확인","확인");
        }
        //Thread.sleep(5000);
        return contractAddress;
    }



    public int mint_NFT(String img ,String minter_private_key, String address_to, String contractAddress) throws InterruptedException {
        Caver caver = new Caver(BAO_API);
        AtomicInteger ret = new AtomicInteger(-1);
        new Thread(() -> {
            try {
                SingleKeyring keyring = (SingleKeyring) caver.wallet.keyring.createFromPrivateKey(minter_private_key);
                caver.wallet.add(keyring);
                KIP17 kip17 = new KIP17(caver, contractAddress);

                BigInteger token_id = kip17.totalSupply();
                ret.set(token_id.intValue());
                //현재 총 발행수가 토큰 id가 됨.

                SendOptions newSendOptions = new SendOptions();
                newSendOptions.setFrom(keyring.getAddress());
                newSendOptions.setGas(BigInteger.valueOf(55550000));
                kip17.mintWithTokenURI(address_to, token_id, img, newSendOptions);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (TransactionException e) {
                e.printStackTrace();
            }
        }).start();
        while(ret.get()==-1)
        {
            Thread.sleep(1000);
            Log.e("타이머반복확인","확인");
        }
        Thread.sleep(1000);
        return ret.get();
    }


    public String get_NFT_info(Integer token_id) throws InterruptedException {
        AtomicReference<String> ret = new AtomicReference<>("");
        new Thread(() -> {
            try {
                KIP17 kip17 = new KIP17(caver, contract_address);
                ret.set(kip17.tokenURI(BigInteger.valueOf(token_id)));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(1000);
        return String.valueOf(ret);
    }

    public boolean is_owner(BigInteger tokenId, String newAddress) throws InterruptedException {
        AtomicReference<String> owner = new AtomicReference<>("");
        new Thread(() -> {
            try {
                KIP17 kip17 = new KIP17(caver, contract_address);
                owner.set(kip17.ownerOf(tokenId));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(2500);
        if (owner.get().equals(newAddress)) {
            return true;
        }
        return false;
    }

    public void send_KLAY(String senderPrivateKey, String destination, String memo, String amount) throws IOException, CipherException, TransactionException, InterruptedException {
        String input = Numeric.toHexString(memo.getBytes(StandardCharsets.UTF_8));
        BigInteger value = new BigInteger(amount).multiply(new BigInteger("1000000000000000000"));

        Caver caver = new Caver(BAO_API);

        SingleKeyring keyring = (SingleKeyring) caver.wallet.keyring.createFromPrivateKey(senderPrivateKey);
        caver.wallet.add(keyring);

        //Create a value transfer transaction
        ValueTransferMemo valueTransferMemo = caver.transaction.valueTransferMemo.create(
                TxPropertyBuilder.valueTransferMemo()
                        .setFrom(keyring.getAddress())
                        .setTo(destination)
                        .setValue(value)
                        .setGas(BigInteger.valueOf(5555000))
                        .setInput(input)
        );

        Log.e("현재 보유액:",getBalance(LoginActivity.userAddress));
        Log.e("내야할 금액:",amount);
        if(Double.parseDouble(getBalance(LoginActivity.userAddress))>Double.parseDouble(amount))
        {
            // 스레드를 사용하여 네트워크 요청
            new Thread(() -> {
                try {
                    AbstractTransaction at = caver.wallet.sign(keyring.getAddress(), valueTransferMemo);
                    //Send a transaction to the klaytn blockchain platform (Klaytn)
                    Bytes32 result = caver.rpc.klay.sendRawTransaction(valueTransferMemo.getRawTransaction()).send();
                    if(result.hasError()) {
                        throw new RuntimeException(result.getError().getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        else
        {
            throw new RuntimeException();
        }
        Thread.sleep(1000);
    }

    public void send_NFT(String to, int tokenId) throws InterruptedException {
        new Thread(() -> {
            try {
                BigInteger tokenID = BigInteger.valueOf(tokenId);
                SingleKeyring keyring = (SingleKeyring) caver.wallet.keyring.createFromPrivateKey(private_key);
                //컨트랙트 배포자의 권한으로 토큰전송, 트랜잭션 주도.

                Caver caver = new Caver(BAO_API);
                caver.wallet.add(keyring);
                KIP17 kip17 = new KIP17(caver, contract_address);
                String from = kip17.ownerOf(BigInteger.valueOf(tokenId));
                Thread.sleep(1500); // 바로 owner의 주소가 안 올 수 있다.

                SendOptions newSendOptions = new SendOptions();
                newSendOptions.setFrom(keyring.getAddress());
                newSendOptions.setGas(BigInteger.valueOf(55550000)); //가스비 조정
                kip17.safeTransferFrom(from,to,tokenID,newSendOptions);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (TransactionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void send_NFT(String fromPrivateKey, String to, int tokenId) throws InterruptedException {
        new Thread(() -> {
            try {
                BigInteger tokenID = BigInteger.valueOf(tokenId);
                Caver caver = new Caver(BAO_API);
                SingleKeyring keyring = (SingleKeyring) caver.wallet.keyring.createFromPrivateKey(fromPrivateKey);
                caver.wallet.add(keyring);
                KIP17 kip17 = new KIP17(caver, contract_address);
                String from = kip17.ownerOf(BigInteger.valueOf(tokenId));
                Thread.sleep(1500); // 바로 owner의 주소가 안 올 수 있다.

                SendOptions newSendOptions = new SendOptions();
                newSendOptions.setFrom(keyring.getAddress());
                newSendOptions.setGas(BigInteger.valueOf(55550000)); //가스비 조정
                kip17.safeTransferFrom(from,to,tokenID,newSendOptions);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (TransactionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(2500);
    }

    public void send_Token(String fromPrivateKey, String to, String amount) throws InterruptedException, IOException{
        if(Double.parseDouble(getBalanceToken(LoginActivity.userAddress))>=Double.parseDouble(amount)) {
            new Thread(() -> {
                try {
                    if (amount.equals("0000"))
                        return;
                    Caver caver = new Caver(BAO_API);
                    SingleKeyring keyring = (SingleKeyring) caver.wallet.keyring.createFromPrivateKey(fromPrivateKey);
                    caver.wallet.add(keyring);
                    KIP7 kip7 = new KIP7(caver, contract_address_token);

                    SendOptions newSendOptions = new SendOptions();
                    newSendOptions.setFrom(keyring.getAddress());
                    newSendOptions.setGas(BigInteger.valueOf(55550000)); //가스비 조정
                    kip7.safeTransfer(to, new BigInteger(amount).multiply(new BigInteger("10000")), newSendOptions);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (TransactionException e) {
                    e.printStackTrace();
                }
            }).start();
        }else
        {
            throw new RuntimeException();
        }
        Thread.sleep(1000);
    }

    public boolean search_ticket(String contractAddress, String userAddress) throws InterruptedException {
        AtomicReference<String> owner = new AtomicReference<>("");
        new Thread(() -> {
            try {
                KIP17 kip17 = new KIP17(caver, contractAddress);
                BigInteger total=kip17.totalSupply();
                Log.e("전체 튜플수", String.valueOf(total));
                BigInteger bigNumber1=new BigInteger("0");
                BigInteger bigNumber2=new BigInteger("1");
                String tempAddress="0x720128aef3202f6ee59c3e2904a3bab17fd92552";
                /*if (kip17.ownerOf(new BigInteger("80")).equals(userAddress)) {
                    result=true;
                }*/
                /*while(bigNumber1.compareTo(total)!=0)
                {
                    //owner.set(kip17.ownerOf(bigNumber1));
                    if ((kip17.ownerOf(bigNumber1)).equals(userAddress)) {
                        result=true;
                        break;
                    }
                    bigNumber1.add(bigNumber2);

                }*/
                kip17.tokenOwnerByIndex(userAddress,new BigInteger("0"));
                Log.e("검사", String.valueOf(kip17.tokenOwnerByIndex(userAddress,new BigInteger("0"))));
                result=true;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IOException e) {
                result=false;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(2500);
        if(result==true)
        {
            result=false;
            return true;
        }
        return false;
    }

    public String getBalance(String address) throws InterruptedException, IOException {
        caver = new Caver(BAO_API);
        AtomicReference<BigInteger> balance = new AtomicReference<>(null);
        new Thread(() -> {
            try {
                balance.set(caver.rpc.klay.getBalance(address).send().getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(1000);
        String ret = caver.utils.convertFromPeb(String.valueOf(balance.get()), "KLAY");
        return ret;
    }

    public String getBalanceToken(String address) throws InterruptedException, IOException {
        caver = new Caver(BAO_API);
        AtomicReference<String> balance = new AtomicReference<>(null);
        new Thread(() -> {
            try {
                Caver caver = new Caver(BAO_API);
                KIP7 kip7 = new KIP7(caver, contract_address_token);
                balance.set(kip7.balanceOf(address).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(1000);
        BigDecimal ret = new BigDecimal(balance.get());
        ret = ret.divide(new BigDecimal("10000"));
        return ret.toString();
    }
}
